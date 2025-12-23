package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.Department;
import com.hrms.entity.Employee;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author System
 * @since 2024-05-22
 */
public interface DepartmentRepository extends BaseMapper<Department> {
    @Select("""
            SELECT *
            FROM department
            WHERE manager_id = #{managerId}
            """)
    List<Department> findByManagerId(@Param("managerId") Integer managerId);

    @Select("""
                SELECT e.*
                FROM department d
                LEFT JOIN employee e ON d.manager_id = e.id
                WHERE d.id = #{departmentId}
            """)
    Employee findManagerByDepartmentId(@Param("departmentId") Integer departmentId);

    @Select("""
                SELECT e.*
                FROM department d
                INNER JOIN employee e ON d.id = e.department_id
                WHERE d.department_name LIKE CONCAT('%', #{keyword}, '%')
                  AND e.status = 1
            """)
    List<Employee> findEmployeesByDepartmentName(@Param("keyword") String keyword);

    @Select("""
        SELECT CASE
                 WHEN #{shiftTypes} = 'MORNING' THEN every_day_morning_count
                 WHEN #{shiftTypes} = 'AFTERNOON' THEN every_day_afternoon_count
                 WHEN #{shiftTypes} = 'NIGHT' THEN every_day_night_count
                 ELSE 0
               END
          FROM department
         WHERE id = #{deptId}
    """)
    int findMinRequired(@Param("deptId") int deptId,
                        @Param("shiftTypes") String shiftTypes);
}
