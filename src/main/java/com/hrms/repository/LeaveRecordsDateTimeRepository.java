package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.LeaveRecordsDateTime;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author System
 * @since 2024-08-16
 */
public interface LeaveRecordsDateTimeRepository extends BaseMapper<LeaveRecordsDateTime> {

    @Select("""
            SELECT * 
            FROM leave_records_date_time 
            WHERE leave_records_id = #{leaveRecordsId}
            """)
    List<LeaveRecordsDateTime> findByLeaveRecordsId(@Param("leaveRecordsId") Integer leaveRecordsId);

    @Select("""
select * from leave_records_date_time where start_date >= #{startDate} and end_date <= #{endDate} and exists(
    select * from leave_records where employee_id = #{employeeId} and id = leave_records_date_time.leave_records_id and status = 1
)
order by start_date
""")
    List<LeaveRecordsDateTime> findByDate(Integer employeeId, LocalDateTime startDate, LocalDateTime endDate);
}
