package com.hrms.entity.mapstruct;

import com.hrms.entity.Department;
import com.hrms.entity.Employee;
import com.hrms.entity.ShiftSchedules;
import com.hrms.model.bo.ShiftSchedulesBO;
import com.hrms.model.vo.ShiftSchedulesVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ShiftSchedulesMapper {

    ShiftSchedulesMapper INSTANCE = Mappers.getMapper(ShiftSchedulesMapper.class);

    @Mapping(source = "employee.nickName", target = "nickName")
    @Mapping(source = "employee.departmentId", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    @Mapping(source = "shiftSchedules.id", target = "id")
    @Mapping(source = "shiftSchedules.employeeId", target = "employeeId")
    @Mapping(source = "shiftSchedules.shiftDate", target = "shiftDate")
    @Mapping(source = "shiftSchedules.shiftTypes", target = "shiftTypes")
    @Mapping(source = "shiftSchedules.status", target = "status")
    @Mapping(source = "shiftSchedules.weekType", target = "weekType")
    @Mapping(source = "shiftSchedules.remark", target = "remark")
    @Mapping(source = "shiftSchedules.actionType", target = "actionType")
    ShiftSchedulesVO shiftSchedulesToShiftSchedulesVO(ShiftSchedules shiftSchedules, Employee employee, Department department);

    ShiftSchedules shiftSchedulesBOToShiftSchedules(ShiftSchedulesBO bo);

    ShiftSchedulesBO shiftSchedulesToShiftSchedulesBO(ShiftSchedules shiftSchedules);
}
