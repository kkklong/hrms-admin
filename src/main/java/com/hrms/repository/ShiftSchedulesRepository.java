package com.hrms.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hrms.entity.ShiftSchedules;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.model.RemoteAuditShift;
import com.hrms.model.excel.ShiftSchedulesConfig;
import com.hrms.model.excel.ShiftSchedulesExcel;
import com.hrms.model.vo.EmployeeShiftSchedulesVO;
import com.hrms.model.vo.ShiftSchedulePeriodHolidayVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author System
 * @since 2025-04-13
 */
public interface ShiftSchedulesRepository extends BaseMapper<ShiftSchedules> {

    @Select("SELECT COUNT(1) FROM shift_schedules WHERE shift_date = #{date}")
    boolean existsByShiftDate(@Param("date") LocalDate date);

    @Select("""
            SELECT s.*
            FROM shift_schedules s left join employee e
			on s.employee_id = e.id
            WHERE s.department_id = #{departmentId} and e.status = 1
            AND shift_date BETWEEN #{startDate} AND #{endDate}
            """)
    List<ShiftSchedules> queryByDepartmentAndDateRange(@Param("departmentId") int departmentId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    @Select("SELECT week_type FROM shift_schedules WHERE employee_id = 1 and shift_date = #{date}")
    Byte getWeekType(LocalDate localDate);

    @Select("SELECT * FROM shift_schedules WHERE shift_date = #{date} AND employee_id = #{employeeId}")
    ShiftSchedules getByDateAndEmployeeId(LocalDate date, Integer employeeId);

    @Select("""
            SELECT b.department_name, c.nick_name, GROUP_CONCAT(d.config_value5 order by a.shift_date asc) as shift_color_codes
                    FROM shift_schedules a JOIN department b 
            		on a.department_id = b.id 
            		JOIN employee c 
            		on a.employee_id = c.id 
            		JOIN config d 
            		on a.shift_types = d.config_key 
            ${ew.customSqlSegment}
//            and c.role_id != 1
            group by b.department_name, c.nick_name
            """)
    List<ShiftSchedulesExcel> queryShiftSchedulesExcel(@Param(Constants.WRAPPER) QueryWrapper<ShiftSchedules> ew);


    @Select("""
           SELECT b.department_name, c.`name` as config_name, c.config_value5 as shift_color_code
                FROM shift_schedules a JOIN department b 
                on a.department_id = b.id 
                JOIN config c 
                on a.shift_types = c.config_key 
           ${ew.customSqlSegment}
           group by b.department_name, c.`name`, c.config_value5
           order by b.department_name
           """)
    List<ShiftSchedulesConfig> querySchedulesConfig(@Param(Constants.WRAPPER) QueryWrapper<ShiftSchedules> ew);


    @Select("""
            select shift_date, remark, shift_types
            from shift_schedules
            where employee_id = 1
              and shift_types like '%HOLIDAY%'
              and shift_date between #{startDate} and #{endDate}
            order by shift_date
            """)
    List<ShiftSchedulePeriodHolidayVo> queryDefaultHolidays(LocalDate startDate, LocalDate endDate);

    @Select("""
            SELECT s.shift_types, s.shift_date, s.status, c.name as shift_name
            FROM shift_schedules s left join employee e
			on s.employee_id = e.id
            left join config c
            on s.shift_types = c.config_key
            WHERE s.employee_id = #{employeeId} 
            AND e.status = 1
            AND shift_date BETWEEN #{startDate} AND #{endDate}
            """)
    List<EmployeeShiftSchedulesVO> queryByEmployeeAndDateRange(@Param("employeeId") int employeeId,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate);

    @Select("""
        SELECT s.*
        FROM shift_schedules s
        JOIN employee e ON e.id = s.employee_id AND e.status = 1
        WHERE s.employee_id = #{employeeId}
          AND s.shift_date BETWEEN #{startDate} AND #{endDate}
        ORDER BY s.shift_date
        """)
    List<ShiftSchedules> findByEmployeeIdAndShiftDateBetween(@Param("employeeId") int employeeId,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    /** 計算可上工人數 */
    @Select("""
        SELECT COUNT(*) FROM shift_schedules
         WHERE department_id = #{deptId}
           AND shift_types = #{shiftTypes}
           AND shift_date = #{shiftDate}
           AND status = 0
    """)
    int countAvailable(@Param("deptId") int deptId,
                       @Param("shiftTypes") String shiftTypes,
                       @Param("shiftDate") LocalDate shiftDate);

    /** 清除暫鎖 */
    @Update("""
        UPDATE shift_schedules
           SET review_lock_request_id = NULL
         WHERE review_lock_request_id = #{reqId}
    """)
    void clearReviewLock(@Param("reqId") Long reqId);

    /** 查暫鎖班表 + 排他鎖 */
    @Select("""
        SELECT * FROM shift_schedules
         WHERE review_lock_request_id = #{reqId}
         FOR UPDATE
    """)
    List<ShiftSchedules> selectByReviewLockRequestIdForUpdate(@Param("reqId") Long reqId);

    /** 單向更新班別 */
    @Update("""
        UPDATE shift_schedules
           SET shift_types = #{targetShiftTypes},
               department_id = COALESCE(#{targetDepartmentId}, department_id),
               shift_date = #{targetDate},
               updated_id = #{updatedId},
               updated_date = NOW()
         WHERE id = #{originScheduleId}
    """)
    void updateShiftToTarget(@Param("originScheduleId") Long originScheduleId,
                             @Param("targetShiftTypes") String targetShiftTypes,
                             @Param("targetDepartmentId") Integer targetDepartmentId,
                             @Param("targetDate") LocalDate targetDate,
                             @Param("updatedId") Integer updatedId);

    /** 互換班別 */
    @Update("""
        UPDATE shift_schedules AS s1
          JOIN shift_schedules AS s2
            ON s1.id = #{idA} AND s2.id = #{idB}
         SET s1.shift_types = s2.shift_types,
             s2.shift_types = s1.shift_types
    """)
    void swapShiftBetween(@Param("idA") Long idA, @Param("idB") Long idB);

    void updateReviewLock(@Param("reqId") Long reqId, @Param("ids") List<Long> ids);

    /**
     * 查询指定日期的班次安排信息
     * 该查询从班次表(shift_schedules)中获取特定日期的班次信息，
     * 并关联远程考勤期表(remote_attendance_period)和配置表(config)
     * 以获取完整的班次时间信息
     *
     * @param dateD 查询的日期参数
     * @return 返回包含班次ID、日期、类型、开始时间和结束时间的列表
     */
    @Select("""
              SELECT
                ss.employee_id,         -- 员工ID
                ss.shift_date,          -- 班次日期
                ss.shift_types,         -- 班次类型
                TIMESTAMP(ss.shift_date, c.config_value1) AS startTime,  -- 开始时间(日期+时间)
                CASE
                    WHEN TIME(c.config_value1) > TIME(c.config_value2)
                    THEN TIMESTAMP(DATE_ADD(ss.shift_date, INTERVAL 1 DAY), c.config_value2)
                    ELSE TIMESTAMP(ss.shift_date, c.config_value2)
                END AS endTime     -- 结束时间(日期+时间)
            FROM shift_schedules ss                     -- 班次安排表
                     JOIN remote_attendance_period rap  -- 远程考勤期表
                          ON rap.remote_date = ss.shift_date  -- 通过日期关联
                     JOIN config c ON c.config_key = ss.shift_types  -- 通过班次类型关联配置
            WHERE ss.status = 0                        -- 只查询状态为0的记录
              AND ss.shift_date = #{dateD}               -- 指定查询日期
              AND FIND_IN_SET(ss.shift_types, rap.available_shift_type) > 0  -- 班次类型在远程考勤期范围内
              AND (
                  -- 情况1: 不跨日班次，当前时间在同一天的开始和结束时间之间
                  (TIME(c.config_value1) <= TIME(c.config_value2)
                   AND NOW() BETWEEN TIMESTAMP(ss.shift_date, c.config_value1)\s
                                 AND TIMESTAMP(ss.shift_date, c.config_value2))
                  OR
                  -- 情况2: 跨日班次，当前时间在开始时间到次日结束时间之间
                  (TIME(c.config_value1) > TIME(c.config_value2)
                   AND NOW() BETWEEN TIMESTAMP(ss.shift_date, c.config_value1)
                                 AND TIMESTAMP(DATE_ADD(ss.shift_date, INTERVAL 1 DAY), c.config_value2))
              )
            
            """)
    @Cacheable(value = "shiftSchedules", key = "#dateD")
    List<RemoteAuditShift> queryRemoteAttendancePeriod(@Param("dateD") LocalDate dateD);

}
