package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.LeaveSpecialRecords;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author System
 * @since 2024-06-08
 */
public interface LeaveSpecialRecordsRepository extends BaseMapper<LeaveSpecialRecords> {

    @Select("""
            SELECT * 
            FROM leave_special_records 
            WHERE employee_id = #{employeeId}
            AND end_date > NOW()
            """)
    List<LeaveSpecialRecords> getByEmployeeId(@Param("employeeId") int employeeId);

    @Select("""
        SELECT *
        FROM leave_special_records
        WHERE employee_id = #{employeeId}
          AND leave_types = #{leaveTypes}
          AND end_date > NOW()
        """)
    List<LeaveSpecialRecords> getByEmployeeIdAndLeaveType(@Param("employeeId") int employeeId,
                                                    @Param("leaveTypes") String leaveTypes);

    @Update("""
        UPDATE leave_special_records a,
        (
        	select ls.employee_id, ls.max_leave_days* 8 as max_leave_days, ls.start_date, ls.end_date, sum(ld.count_val) as sum_count_val
        	from leave_special_records ls left join leave_records lr
        	on ls.employee_id = lr.employee_id
        	left join leave_records_date_time ld
        	on lr.id = ld.leave_records_id
        	where ld.start_date > ls.start_date and ld.start_date < ls.end_date
        	and DATE_FORMAT(ls.end_date,"%Y-%m") = #{settlementDate} and lr.leave_types = 0
            and lr.status not in (2,3)
        	GROUP BY  ls.employee_id, ls.max_leave_days, ls.start_date, ls.end_date
        ) as b
        set a.settlement_date = #{settlementDate},
        a.settlement_count = IF(b.max_leave_days > b.sum_count_val, (b.max_leave_days - b.sum_count_val)/8, 0)
        where a.employee_id = b.employee_id
        and DATE_FORMAT(a.end_date,"%Y-%m") = #{settlementDate}
        and a.leave_types = 0
        and a.start_date = b.start_date and a.end_date = b.end_date
        """)
    void updateBySettlement(String settlementDate);
}
