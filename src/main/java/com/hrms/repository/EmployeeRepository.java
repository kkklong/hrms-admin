package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.Employee;
import com.hrms.model.RolePermission;
import com.hrms.model.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface EmployeeRepository extends BaseMapper<Employee> {

    @Select("""
            Select * from employee 
            where account = #{account}
            """)
    Employee getByAccount(@Param("account") String account);

    @Select("""
            Select a.account, a.role_id, b.role_name, b.menu_permission 
            from employee a inner join role b on a.role_id = b.id where a.account = #{account}
            """)
    RolePermission getRolePermission(@Param("account") String account);

    @Select("""
            select a.id, a.account, a.full_name, a.nick_name, a.position, 
            a.gender, a.birthday, a.phone, a.email, a.skype, a.telegram, a.entry_date, a.role_id, b.department_parent, b.department_name, 
            b.description, b.manager_nick_name, a.department_id ,
            a.emergency_contact, a.address, a.relationship,
            a.emergency_contact_phone, a.id_number, a.employee_number,
            a.highest_education_level, a.emergency_contact_address, a.registered_address,a.overtime_type, a.company
            from employee a left join department b on a.department_id = b.id where a.account = #{account}
            """)
    UserInfo getUserInfo(@Param("account") String account);
}
