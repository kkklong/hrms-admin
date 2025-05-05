package com.hrms.entity.mapstruct;

import com.hrms.entity.Department;
import com.hrms.model.bo.DepartmentBO;
import com.hrms.model.vo.DepartmentVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DepartmentMapper {
    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    Department CreateDepartmentBOToDepartment(DepartmentBO departmentBO);

    DepartmentVO DepartmentToDepartmentVo(Department department);
}
