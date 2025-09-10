package com.hrms.entity.mapstruct;

import com.hrms.entity.LeaveRecordsDateTime;
import com.hrms.model.bo.LeaveRecordsDateTimeBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LeaveRecordsDateTimeMapper {

    LeaveRecordsDateTimeMapper INSTANCE = Mappers.getMapper(LeaveRecordsDateTimeMapper.class);

    LeaveRecordsDateTime LeaveRecordsDateTimeBOToLeaveRecordsDateTime(LeaveRecordsDateTimeBO leaveRecordsDateTimeBO);
}
