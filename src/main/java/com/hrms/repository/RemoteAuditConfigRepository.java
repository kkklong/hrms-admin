package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.RemoteAuditConfig;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;

public interface RemoteAuditConfigRepository extends BaseMapper<RemoteAuditConfig> {
    @Select("select * from remote_audit_config limit 1")
    @Cacheable(value = "remoteAuditConfig", key = "'rule'")
    RemoteAuditConfig queryRule();


}
