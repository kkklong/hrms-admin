package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.Employee;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface EmployeeRepository extends BaseMapper<Employee> {

    @Select("""
            Select * from employee 
            where account = #{account}
            """)
    Employee getByAccount(@Param("account") String account);

}
