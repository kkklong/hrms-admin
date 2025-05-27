package com.hrms.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.Employee;
import com.hrms.entity.Notice;
import com.hrms.entity.SubmitNotification;
import com.hrms.entity.mapstruct.NoticeMapper;
import com.hrms.entity.mapstruct.SubmitNotificationMapper;
import com.hrms.enums.ErrorCode;
import com.hrms.enums.NoticeStatus;
import com.hrms.exception.ServiceException;
import com.hrms.model.bo.NoticeBO;
import com.hrms.repository.NoticeRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服務實現類
 * </p>
 *
 * @author System
 * @since 2024-06-08
 */
@Slf4j
@Service
@Transactional
public class NoticeService extends ServiceImpl<NoticeRepository, Notice> {

    @Resource
    private NoticeRepository noticeRepository;

    @Resource
    EmployeeService employeeService;

    @Resource
    private EmailService emailService;

    @Resource
    private FileDataService fileDataService;

    @Resource
    private SubmitNotificationService submitNotificationService;

    public Notice create(NoticeBO noticeBO, MultipartFile file) throws IOException {
        Notice notice = NoticeMapper.INSTANCE.noticeBoToNotice(noticeBO);

        if (notice.getId() != null)
            throw new ServiceException(ErrorCode.ID_AUTO_INCREMENT);

        // 公告結束時間不能早於發布時間
        if (notice.getEndDate().isBefore(notice.getPublishDate())) {
            throw new ServiceException(ErrorCode.NOTICE_END_TIME_EARLIER_THAN_START);
        }
        notice.setStatus(NoticeStatus.UNPUBLISHED.getValue());
        noticeRepository.insert(notice);
        if (file != null)
            fileDataService.uploadFile(file, "notice", notice.getId());

        return notice;
    }

    public void update(Notice notice) {
        if (!isNoticeExists(notice.getId()))
            throw new ServiceException(ErrorCode.NOTICE_ID_NOT_FOUND);
        // 公告結束時間不能早於發布時間
        if (notice.getEndDate().isBefore(notice.getPublishDate())) {
            throw new ServiceException(ErrorCode.NOTICE_END_TIME_EARLIER_THAN_START);
        }
        noticeRepository.updateById(notice);
    }

    public void update(Notice notice, MultipartFile file) throws IOException {
        if (!isNoticeExists(notice.getId()))
            throw new ServiceException(ErrorCode.NOTICE_ID_NOT_FOUND);
        // 公告結束時間不能早於發布時間
        if (notice.getEndDate().isBefore(notice.getPublishDate())) {
            throw new ServiceException(ErrorCode.NOTICE_END_TIME_EARLIER_THAN_START);
        }
        noticeRepository.updateById(notice);
        if (file != null)
            fileDataService.uploadFile(file, "notice", notice.getId());
    }

    public void deleteById(Integer id) {
        if (!isNoticeExists(id))
            throw new ServiceException(ErrorCode.NOTICE_ID_NOT_FOUND);
        int rows = noticeRepository.deleteById(id);
        if (rows != 1) {
            throw new ServiceException(ErrorCode.FAILURE);
        }
    }

    private boolean isNoticeExists(Integer noticeId) {
        return noticeRepository.selectById(noticeId) != null;
    }

    public void deleteBatch(List<Integer> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            throw new ServiceException(ErrorCode.INVALID_PARAMETER);
        }
        int rows = noticeRepository.deleteBatchIds(noticeIds);
        if (rows == 0) {
            throw new ServiceException(ErrorCode.NOTICE_ID_NOT_FOUND);
        }
    }

    public void saveAndSend(NoticeBO noticeBO, MultipartFile file) throws IOException {
        Notice notice;
        if (noticeBO.getId() == null) {
            notice = create(noticeBO, file);
        } else {
            update(NoticeMapper.INSTANCE.noticeBoToNotice(noticeBO), file);
            notice = NoticeMapper.INSTANCE.noticeBoToNotice(noticeBO);
        }
        sendMail(notice);
        notice.setStatus(NoticeStatus.PUBLISHED.getValue());
        update(notice);
    }

    public void enable(Integer id) {
        if (!isNoticeExists(id))
            throw new ServiceException(ErrorCode.NOTICE_ID_NOT_FOUND);
        noticeRepository.enableStatus(id);
    }

    public void disable(Integer id) {
        if (!isNoticeExists(id))
            throw new ServiceException(ErrorCode.NOTICE_ID_NOT_FOUND);
        noticeRepository.disableStatus(id);
    }

    public void autoPublishNotices() {
        log.info("排程公告檢查開始，檢查未發布的公告");
        // 查詢所有到發布時間但未發布的公告
        List<Notice> pendingNotices = noticeRepository.selectPendingNotices(LocalDateTime.now(), NoticeStatus.UNPUBLISHED.getValue());

        if (pendingNotices.isEmpty()) {
            log.info("未找到需要發布的公告，排程結束");
            return;
        }

        log.info("需要發布的公告數量: {}", pendingNotices.size());

        for (Notice notice : pendingNotices) {
            try {
                sendMail(notice);
                notice.setStatus(NoticeStatus.PUBLISHED.getValue());
                update(notice);
                log.info("公告 : {} 發布成功", notice.getTitle());
            } catch (Exception e) {
                log.error("公告 : {} 發佈失敗，系統錯誤: {}", notice.getTitle(), e.getMessage(), e);
            }
        }
        log.info("排程公告檢查結束");
    }

    private void sendMail(Notice notice) {
        List<Employee> employees = employeeService.list();
        List<SubmitNotification> notifications = employees.stream().map(employee -> {
            SubmitNotification notification = SubmitNotificationMapper.INSTANCE.toSubmitNotification(notice, employee);
            try {
                if (employee.getEmail() != null && !employee.getEmail().isEmpty()) {
                    emailService.sendEmail(employee.getEmail(), notice.getTitle(), "公告內容：" + notice.getContent());
                    notification.setStatus(NoticeStatus.PUBLISHED.getValue());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                notification.setStatus(NoticeStatus.UNPUBLISHED.getValue()); // 郵件發送失敗，設置狀態為0
            }
            return notification;
        }).toList();
        submitNotificationService.saveBatch(notifications);
    }
}
