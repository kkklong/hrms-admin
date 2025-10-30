package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.Employee;
import com.hrms.entity.RawAttendanceRecords;
import com.hrms.model.bo.RawAttendanceRecordsBO;
import com.hrms.model.vo.RawAttendanceRecordsVO;
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
 * @since 2024-10-05
 */
public interface RawAttendanceRecordsRepository extends BaseMapper<RawAttendanceRecords> {

    List<RawAttendanceRecordsVO> query(RawAttendanceRecordsBO rawAttendanceRecordsBO);

    List<RawAttendanceRecordsVO> queryAll(RawAttendanceRecordsBO rawAttendanceRecordsBO);

    List<RawAttendanceRecordsVO> queryRemote(RawAttendanceRecordsBO rawAttendanceRecordsBO);

    List<RawAttendanceRecordsVO> queryAllRemote(RawAttendanceRecordsBO rawAttendanceRecordsBO);

    RawAttendanceRecordsVO getEmployeeAttendance(LocalDateTime startDate, LocalDateTime endDate, Integer employeeId);

    @Select("""
            SELECT e.* from shift_schedules s join employee e on s.employee_id = e.id
            where s.status = 0 and s.shift_date = #{shiftDate} and s.shift_types = #{shiftType} and not exists(
                select * from raw_attendance_records r where r.account = e.account and r.date_time between #{startDateTime} and #{endDateTime}
            )
            """)
    List<Employee> queryNonClock(LocalDate shiftDate, String shiftType, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
