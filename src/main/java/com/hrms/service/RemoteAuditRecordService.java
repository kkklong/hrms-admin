package com.hrms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.constant.RedisKeyConstants;
import com.hrms.entity.RemoteAuditRecord;
import com.hrms.enums.RemoteAuditStatus;
import com.hrms.model.bo.RemoteAuditRecordDataBO;
import com.hrms.model.vo.RemoteAuditRecordVO;
import com.hrms.repository.RemoteAuditRecordRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional

public class RemoteAuditRecordService extends ServiceImpl<RemoteAuditRecordRepository, RemoteAuditRecord> {
    @Resource
    private RemoteAuditRecordRepository remoteAuditRecordRepository;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public List<RemoteAuditRecordVO> queryRecord(RemoteAuditRecordDataBO bo){
        LambdaQueryWrapper<RemoteAuditRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(bo.getEmployeeId() != null, RemoteAuditRecord::getEmployeeId, bo.getEmployeeId())
                .ge(bo.getStartAuditTime()!=null , RemoteAuditRecord::getSentAt, bo.getStartAuditTime())
                .le(bo.getEndAuditTime()!=null , RemoteAuditRecord::getSentAt, bo.getEndAuditTime())
                .ge(bo.getStartDeadlineTime()!=null , RemoteAuditRecord::getDeadlineAt, bo.getStartDeadlineTime())
                .le(bo.getEndDeadlineTime()!=null , RemoteAuditRecord::getDeadlineAt, bo.getEndDeadlineTime())
                .ge(bo.getStartReplyTime()!=null, RemoteAuditRecord::getRepliedAt, bo.getStartReplyTime())
                .le(bo.getEndReplyTime()!=null, RemoteAuditRecord::getRepliedAt, bo.getEndReplyTime())
                .apply(bo.getStatus()!=null, "a.status = {0}", bo.getStatus())
                .orderByDesc(RemoteAuditRecord::getSentAt);


        List<RemoteAuditRecordVO> voList = remoteAuditRecordRepository.queryRecord(queryWrapper);

        voList.forEach(record ->
                record.setStatusText(
                        RemoteAuditStatus.getDescriptionByValue(record.getStatus().byteValue())
                )
        );
        return voList;
    }
    @Cacheable(value = "remoteAuditRecord", key = "#employeeId")
    public List<RemoteAuditRecord> queryRecent(int employeeId){
        LocalDateTime hoursAgo = LocalDateTime.now().minusHours(12);
        return remoteAuditRecordRepository.selectList(
                new LambdaQueryWrapper<RemoteAuditRecord>()
                        .eq(RemoteAuditRecord::getEmployeeId, employeeId)
                        .ge(RemoteAuditRecord::getSentAt, hoursAgo)
                        .orderByAsc(RemoteAuditRecord::getSentAt));
    }


    public boolean updateRemoteRecordStatus(String uuid) {
        // 1. 先获取需要更新的记录
        RemoteAuditRecord record = remoteAuditRecordRepository.selectOne(
                new LambdaQueryWrapper<RemoteAuditRecord>()
                        .eq(RemoteAuditRecord::getSessionToken, uuid)
        );

        if (record == null) {
            log.info("未找到該筆紀錄，UUID: {}", uuid);
            return false;
        }

        // 2. 更新记录状态
        // 先检查是否超时
        boolean isTimeout = remoteAuditRecordRepository.exists(
                new QueryWrapper<RemoteAuditRecord>()
                        .eq("session_token", uuid)
                        .lt("deadline_at", LocalDateTime.now())  // deadline_at < now 表示已超时
        );
        // 根据是否超时设置不同的状态
        int status = isTimeout ? RemoteAuditStatus.TIMEOUT.getValue() : RemoteAuditStatus.RESPONDED.getValue();
        int updateSuccess = remoteAuditRecordRepository.update(
                new UpdateWrapper<RemoteAuditRecord>()
                        .set("status", status)
                        .set("replied_at", LocalDateTime.now())
                        .eq("session_token", uuid)
        );

        if (updateSuccess > 0) {
            log.info("成功更新稽核狀態，UUID: {}", uuid);

            // 3. 清除相关缓存
            Long employeeId = record.getEmployeeId();
            String cacheKey = RedisKeyConstants.REMOTE_AUDIT_RECORD + employeeId;
            redisTemplate.delete(cacheKey);
            log.info("已清除员工ID {} 的審核缓存", employeeId);
        } else {
            log.error("更新稽核狀態失敗，UUID: {}", uuid);
        }
        return !isTimeout;
    }

}
