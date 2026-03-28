package com.hrms.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hrms.entity.RemoteAuditRecord;
import com.hrms.model.vo.RemoteAuditRecordVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RemoteAuditRecordRepository extends BaseMapper<RemoteAuditRecord> {

    @Select("""
            SELECT a.employee_id AS employeeId, b.nick_name AS nickName, a.sent_at AS sentAt, 
                   a.deadline_at AS deadlineAt, a.replied_at AS repliedAt, a.status AS status, '' AS statusText
            FROM remote_audit_record a JOIN employee b 
            ON a.employee_id = b.id ${ew.customSqlSegment}
            """)
    List<RemoteAuditRecordVO> queryRecord(@Param(Constants.WRAPPER) LambdaQueryWrapper<RemoteAuditRecord> ew);
}
