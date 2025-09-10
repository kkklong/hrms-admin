package com.hrms.entity.mapstruct;

import com.hrms.entity.LeaveSpecialRecordsTemplate;
import com.hrms.model.bo.LeaveSpecialRecordsTemplateBO;
import com.hrms.model.vo.LeaveSpecialRecordsTemplateVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LeaveSpecialRecordsTemplateMapper {
    LeaveSpecialRecordsTemplateMapper INSTANCE = Mappers.getMapper(LeaveSpecialRecordsTemplateMapper.class);

    LeaveSpecialRecordsTemplate toLeaveSpecialRecordsTemplate(LeaveSpecialRecordsTemplateBO leaveSpecialRecordsTemplateBO);

    LeaveSpecialRecordsTemplateVO toLeaveSpecialRecordsTemplateVO(LeaveSpecialRecordsTemplate leaveSpecialRecordsTemplate);
}
