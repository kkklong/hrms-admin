package com.hrms.entity.mapstruct;

import com.hrms.entity.Notice;
import com.hrms.model.bo.NoticeBO;
import com.hrms.model.vo.NoticeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface NoticeMapper {
    NoticeMapper INSTANCE = Mappers.getMapper(NoticeMapper.class);

    NoticeVO noticeToNoticeVO(Notice notice);

    Notice noticeBoToNotice(NoticeBO noticeBO);

}
