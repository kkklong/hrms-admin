package com.hrms.repository;

import com.hrms.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.model.vo.EmployeeRolesVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author System
 * @since 2025-04-13
 */
public interface RoleRepository extends BaseMapper<Role> {
    @Select("""
            SELECT a.id, a.account, a.full_name, a.role_id, b.role_name 
            FROM employee a JOIN role b on a.role_id = b.id 
            """)
    List<EmployeeRolesVO> getEmployeeRoles();
}
