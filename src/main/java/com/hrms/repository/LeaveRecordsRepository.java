package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.LeaveRecords;
import com.hrms.model.excel.LeaveRecordsSheet;
import com.hrms.model.excel.OverTimeRecordsSheet;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author System
 * @since 2024-06-08
 */
public interface LeaveRecordsRepository extends BaseMapper<LeaveRecords> {

    List<LeaveRecords> findByEmployeeIdsAndStatus(@Param("employeeIds") List<Integer> employeeIds, @Param("status") List<Integer> status);

    List<LeaveRecords> findPendingLeaveRecordsByDepartments(@Param("approvalStage") int approvalStage, @Param("departmentIds") List<Integer> departmentIds);

    @Select("""
            SELECT * 
            FROM leave_records 
            WHERE approval_stage = #{approvalStage}
            """)
    List<LeaveRecords> findPendingLeaveRecordsByApprovalStage(@Param("approvalStage") int approvalStage);

    @Select("""
            SELECT SUM(ldt.count_val)
            FROM leave_records lr
            JOIN leave_records_date_time ldt ON lr.id = ldt.leave_records_id
            WHERE lr.employee_id = #{employeeId}
            AND  lr.leave_special_records_id = #{leaveSpecialRecordId}
              AND (
                  -- 處理普通傷病假+ 有薪病假 + 生理病假的時數
                  (lr.leave_types = #{leaveTypes}
                   OR (lr.leave_types IN ('3', '4', '5') AND #{leaveTypes} = '3'))
                  -- 處理事假 + 家庭照顧假的時數
                  OR (lr.leave_types IN ('2', '7') AND #{leaveTypes} = '2')
              )
              AND lr.status IN (0, 1)
              AND ldt.start_date >= #{startDate}
              AND ldt.end_date <= #{endDate}
            """)
    Double sumLeaveHoursByTypeAndEmployeeId(@Param("leaveTypes") String leaveTypes,
                                            @Param("employeeId") Integer employeeId,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            @Param("leaveSpecialRecordId") Integer leaveSpecialRecordId);

    @Select("""
            SELECT e.employee_number, e.full_name, DATE_FORMAT(b.start_date, '%Y-%m-%d') as date,
            DATE_FORMAT(b.start_date, '%H:%i') as startDateTime,
            DATE_FORMAT(b.end_date, '%H:%i') as endDateTime,
            b.count_val,
            c.name as type,
            a.leave_types
            FROM leave_records a
            JOIN leave_records_date_time b on a.id = b.leave_records_id
            JOIN employee e on a.employee_id = e.id
            JOIN config c on b.shift_type = c.config_key
            WHERE a.employee_id = #{id}
            AND a.status != 2
            AND a.status != 3
            AND b.start_date >= #{startDate}
            AND b.end_date <= #{endDate} 
            """)
    List<LeaveRecordsSheet> queryLeaveRecordsSheet(@Param("id") Integer id, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("""
            SELECT e.employee_number, e.full_name, o.overtime_date as date,
            DATE_FORMAT(o.start_time, '%H:%i') as startDateTime,
            DATE_FORMAT(o.end_time, '%H:%i') as endDateTime,
            o.count_val,
            '加班' as type,
            o.reason
            FROM overtime_records o
            JOIN employee e on o.employee_id = e.id
            WHERE o.employee_id = #{id}
            AND o.start_time >= #{startDate}
            AND o.end_time <= #{endDate} 
            """)
    List<OverTimeRecordsSheet> queryOverTimeRecordsSheet(@Param("id") Integer id, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
