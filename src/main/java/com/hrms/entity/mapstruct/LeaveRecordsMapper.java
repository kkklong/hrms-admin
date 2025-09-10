package com.hrms.entity.mapstruct;

import com.hrms.entity.Employee;
import com.hrms.entity.LeaveRecords;
import com.hrms.model.bo.LeaveRecordsBO;
import com.hrms.model.vo.FileDataVO;
import com.hrms.model.vo.LeaveRecordsVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LeaveRecordsMapper {

    LeaveRecordsMapper INSTANCE = Mappers.getMapper(LeaveRecordsMapper.class);

    @Mapping(source = "employee.nickName", target = "nickName")
    @Mapping(source = "employee.departmentId", target = "departmentId")
    @Mapping(source = "leaveRecords.id", target = "id")
    @Mapping(source = "leaveRecords.leaveTypes", target = "leaveTypes")
    @Mapping(source = "leaveRecords.createdDate", target = "createdDate")
    @Mapping(source = "leaveRecords.countVal", target = "countVal")
    @Mapping(source = "leaveRecords.reason", target = "reason")
    @Mapping(source = "leaveRecords.status", target = "status")
    @Mapping(source = "leaveRecords.remark", target = "remark")
    @Mapping(source = "leaveRecords.approvalStage", target = "approvalStage")
    @Mapping(source = "leaveRecords.historyReview", target = "historyReview")
    @Mapping(source = "leaveRecords.attachmentRequired", target = "attachmentRequired")
    @Mapping(source = "files", target = "files")
    LeaveRecordsVO leaveRecordsToLeaveRecordsVO(LeaveRecords leaveRecords, Employee employee, List<FileDataVO> files);

    LeaveRecords leaveRecordBOToLeaveRecords(LeaveRecordsBO leaveRecordsBO);
}