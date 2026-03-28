package com.hrms.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.RemoteAuditConfig;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.bo.RemoteAuditConfigBo;
import com.hrms.model.vo.RemoteAuditConfigVO;
import com.hrms.repository.RemoteAuditConfigRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@Transactional

public class RemoteAuditConfigService extends ServiceImpl<RemoteAuditConfigRepository, RemoteAuditConfig> {
    @Resource
    private RemoteAuditConfigRepository remoteAuditConfigRepository;

    public RemoteAuditConfigVO queryRule(){
        RemoteAuditConfig config = remoteAuditConfigRepository.queryRule();
        RemoteAuditConfigVO vo = new RemoteAuditConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }

    public RemoteAuditConfig queryEntity(){
        return remoteAuditConfigRepository.queryRule();
    }

    /**
     * 修改稽查規則設定
     */
    @CacheEvict(value = "remoteAuditConfig", key = "'rule'")
    public void update(RemoteAuditConfigBo item) {
        // 获取现有的规则配置
        RemoteAuditConfig rule = remoteAuditConfigRepository.queryRule();

        // 检查是否有任何参数发生了变化
        boolean hasChanges = false;

        // 比较每个字段
        if (!Objects.equals(rule.getDailyCheckTotal(), item.getDailyCheckTotal())) {
            hasChanges = true;
        }
        if (!Objects.equals(rule.getResponseTimeoutMin(), item.getResponseTimeoutMin())) {
            hasChanges = true;
        }
        if (!Objects.equals(rule.getRetryDelayMin(), item.getRetryDelayMin())) {
            hasChanges = true;
        }
        if (!Objects.equals(rule.getRetryMaxTimes(), item.getRetryMaxTimes())) {
            hasChanges = true;
        }
        if (!Objects.equals(rule.getAlertThresholdMiss(), item.getAlertThresholdMiss())) {
            hasChanges = true;
        }
        if (!Objects.equals(rule.getAlertToLeader(), item.getAlertToLeader())) {
            hasChanges = true;
        }
        if (!Objects.equals(rule.getAlertToHr(), item.getAlertToHr())) {
            hasChanges = true;
        }
        if (!Objects.equals(rule.getExcludeRecentMin(), item.getExcludeRecentMin())) {
            hasChanges = true;
        }

        // 如果没有变化，抛出异常
        if (!hasChanges) {
            throw new ServiceException(ErrorCode.NO_CHANGES_DETECTED);
        }

        // 如果有变化，更新数据库记录
        BeanUtils.copyProperties(item, rule);
        remoteAuditConfigRepository.updateById(rule);
    }
}
