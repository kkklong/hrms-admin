package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.SubmitNotification;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface SubmitNotificationRepository extends BaseMapper<SubmitNotification> {

    @Select("""
             SELECT * FROM submit_notification WHERE notice_id = #{noticeId}
            """)
    List<SubmitNotification> queryByNoticeId(Integer noticeId);

    @Select("""
            SELECT * FROM submit_notification WHERE employee_id = #{employeeId}
            """)
    List<SubmitNotification> queryByEmployeeId(Integer employeeId);

    @Select("""
            SELECT id FROM submit_notification WHERE created_date < #{createdDate}
            """)
    List<Integer> selectIdsByCreatedDateBefore(LocalDateTime createdDate);

    @Select("""
            SELECT id FROM submit_notification WHERE end_date < #{endDate}
            """)
    List<Integer> selectIdsByEndDateBefore(LocalDateTime endDate);
}
