package com.hrms.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.SubmitNotification;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.UserInfo;
import com.hrms.repository.SubmitNotificationRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class SubmitNotificationService extends ServiceImpl<SubmitNotificationRepository, SubmitNotification> {

    @Resource
    private SubmitNotificationRepository submitNotificationRepository;

    //根據公告id獲取所有相關的通知
    public List<SubmitNotification> queryByNoticeId(Integer noticeId) {
        return submitNotificationRepository.queryByNoticeId(noticeId);
    }

    //根據員工id獲取所有相關的通知
    public List<SubmitNotification> queryByEmployeeId(Integer employeeId) {
        return submitNotificationRepository.queryByEmployeeId(employeeId);
    }

    public void update(SubmitNotification submitNotification) {
        if (!isSubmitNotificationExists(submitNotification.getId()))
            throw new ServiceException(ErrorCode.SUBMIT_NOTIFICATION_ID_NOT_FOUND);
        LocalDateTime now = LocalDateTime.now();
        submitNotification.setUpdatedDate(now);
        submitNotificationRepository.updateById(submitNotification);
    }

    private boolean isSubmitNotificationExists(Integer submitNotificationId) {
        return submitNotificationRepository.selectById(submitNotificationId) != null;
    }

    public List<SubmitNotification> queryByCurrentEmployee() {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        return submitNotificationRepository.queryByEmployeeId(userInfo.getId());
    }

    public void deleteBatch(List<Integer> submitNotificationIds) {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Integer userId = userInfo.getId();
        //檢查是否有權限刪除
        List<SubmitNotification> notifications = submitNotificationRepository.selectBatchIds(submitNotificationIds);
        boolean hasUnauthorized = notifications.stream()
                .anyMatch(notification -> !userId.equals(notification.getEmployeeId()));
        if (hasUnauthorized) {
            throw new ServiceException(ErrorCode.NOTIFICATION_DELETE_UNAUTHORIZED);
        }
        int rows = submitNotificationRepository.deleteBatchIds(submitNotificationIds);
        if (rows == 0) {
            throw new ServiceException(ErrorCode.SUBMIT_NOTIFICATION_ID_NOT_FOUND);
        }
    }

    public void deleteExpiredAndOldNotifications() {
        log.info("開始執行排程刪除通知");
        // 查詢90天前創建的通知ID
        List<Integer> oldNotificationIds = submitNotificationRepository.selectIdsByCreatedDateBefore(LocalDateTime.now().minusDays(90));
        if (oldNotificationIds != null && !oldNotificationIds.isEmpty()) {
            log.info("刪除90天前創建的通知，共{}條", oldNotificationIds.size());
            int rows = submitNotificationRepository.deleteBatchIds(oldNotificationIds);
            if (rows > 0) {
                log.info("成功刪除90天前的通知，共刪除{}條", rows);
            } else {
                log.warn("刪除90天前的通知時未刪除任何記錄");
            }
        }
        // 查詢結束時間已過期的通知ID
        List<Integer> expiredNotificationIds = submitNotificationRepository.selectIdsByEndDateBefore(LocalDateTime.now());
        if (expiredNotificationIds != null && !expiredNotificationIds.isEmpty()) {
            log.info("排程刪除已過期的通知，共{}條", expiredNotificationIds.size());
            int rows = submitNotificationRepository.deleteBatchIds(expiredNotificationIds);
            if (rows > 0) {
                log.info("成功刪除已過期的通知，共刪除{}條", rows);
            } else {
                log.warn("刪除已過期的通知時未刪除任何記錄");
            }
        }
        log.info("排程刪除通知執行結束");
    }
}
