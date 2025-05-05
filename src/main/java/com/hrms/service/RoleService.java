package com.hrms.service;

import com.hrms.entity.Role;
import com.hrms.entity.mapstruct.RoleMapper;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.vo.EmployeeRolesVO;
import com.hrms.model.vo.RoleVO;
import com.hrms.repository.RoleRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 *  服務實現類
 * </p>
 *
 * @author System
 * @since 2025-04-13
 */
@Slf4j
@Service
@Transactional
public class RoleService extends ServiceImpl<RoleRepository, Role> {
    public RoleVO query(Integer id) {
        Optional<Role> role = this.getOptById(id);
        if (role.isEmpty()) {
            throw new ServiceException(ErrorCode.ROLE_ERROR);
        }
        RoleVO roleVO = RoleMapper.INSTANCE.toRoleVO(role.get());
        return roleVO;
    }

    public List<EmployeeRolesVO> queryEmployeeRoles() {
        List<EmployeeRolesVO> employeeRolesVOs = this.getBaseMapper().getEmployeeRoles();
        if (employeeRolesVOs.isEmpty()) {
            throw new ServiceException(ErrorCode.ROLE_ERROR);
        }
        return employeeRolesVOs;
    }
}
