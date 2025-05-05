package com.hrms.entity.mapstruct;

import com.hrms.entity.Role;
import com.hrms.model.bo.CreateRoleBO;
import com.hrms.model.bo.UpdateRoleBO;
import com.hrms.model.vo.RoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    Role toRole(CreateRoleBO createRoleBO);

    Role toRole(UpdateRoleBO updateRoleBO);

    RoleVO toRoleVO(Role role);
}
