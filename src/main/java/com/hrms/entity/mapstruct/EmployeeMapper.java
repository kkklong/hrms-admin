package com.hrms.entity.mapstruct;

import com.hrms.entity.Employee;
import com.hrms.model.bo.CreateEmployeeBO;
import com.hrms.model.bo.EditUserInfoBO;
import com.hrms.model.bo.EmployeeBO;
import com.hrms.model.vo.QueryEmployeeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    Employee employeeBOToEmployee(EmployeeBO employeeBO);

    Employee employeeCreateBOToEmployee(CreateEmployeeBO createEmployeeBO);

    QueryEmployeeVO employeeToQueryEmployeeVO(Employee employee);

    Employee editUserInfoBOToEmployee(EditUserInfoBO editUserInfoBO);
}
