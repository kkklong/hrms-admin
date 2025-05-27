package com.hrms.entity.mapstruct;

import com.hrms.entity.Employee;
import com.hrms.entity.Notice;
import com.hrms.entity.SubmitNotification;
import com.hrms.model.bo.SubmitNotificationBO;
import com.hrms.model.vo.SubmitNotificationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubmitNotificationMapper {

    SubmitNotificationMapper INSTANCE = Mappers.getMapper(SubmitNotificationMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "notice.id", target = "noticeId")
    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee.skype", target = "skype")
    @Mapping(source = "notice.title", target = "title")
    @Mapping(source = "notice.content", target = "description")
    @Mapping(target = "status", constant = "1")
    @Mapping(target = "readStatus", constant = "0")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    SubmitNotification toSubmitNotification(Notice notice, Employee employee);

    SubmitNotificationVO submitNotificationToSubmitNotificationVO(SubmitNotification submitNotification);

    SubmitNotification submitNotificationBoToSubmitNotification(SubmitNotificationBO submitNotificationBO);
}
