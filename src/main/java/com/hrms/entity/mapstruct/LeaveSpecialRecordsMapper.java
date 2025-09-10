package com.hrms.entity.mapstruct;

import com.hrms.entity.Department;
import com.hrms.entity.Employee;
import com.hrms.entity.LeaveSpecialRecords;
import com.hrms.model.bo.LeaveSpecialRecordsBO;
import com.hrms.model.vo.LeaveSpecialRecordsVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LeaveSpecialRecordsMapper {

    LeaveSpecialRecordsMapper INSTANCE = Mappers.getMapper(LeaveSpecialRecordsMapper.class);

    LeaveSpecialRecords leaveSpecialRecordsBOToLeaveSpecialRecords(LeaveSpecialRecordsBO leaveSpecialRecordsBO);

    @Mapping(source = "leaveSpecialRecords.id", target = "id")
    @Mapping(source = "leaveSpecialRecords.employeeId", target = "employeeId")
    @Mapping(source = "employee.nickName", target = "nickName")
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    @Mapping(source = "leaveSpecialRecords.leaveTypes", target = "leaveTypes")
    @Mapping(source = "leaveSpecialRecords.salaryStandard", target = "salaryStandard")
    @Mapping(source = "leaveSpecialRecords.fullAttendanceBonus", target = "fullAttendanceBonus")
    @Mapping(source = "leaveSpecialRecords.minLeaveUnit", target = "minLeaveUnit")
    @Mapping(source = "leaveSpecialRecords.maxLeaveDays", target = "maxLeaveDays")
    @Mapping(source = "leaveSpecialRecords.continuousLeave", target = "continuousLeave")
    @Mapping(source = "leaveSpecialRecords.advanceApplication", target = "advanceApplication")
    @Mapping(source = "leaveSpecialRecords.description", target = "description")
    @Mapping(source = "leaveSpecialRecords.settlementDate", target = "settlementDate")
    @Mapping(source = "leaveSpecialRecords.settlementCount", target = "settlementCount")
    @Mapping(source = "leaveSpecialRecords.attachmentRequired", target = "attachmentRequired")
    LeaveSpecialRecordsVO leaveSpecialRecordsToLeaveSpecialRecordsVO(LeaveSpecialRecords leaveSpecialRecords, Employee employee, Department department);
}
