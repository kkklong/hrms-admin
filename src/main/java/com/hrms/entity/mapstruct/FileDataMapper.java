package com.hrms.entity.mapstruct;

import com.hrms.entity.FileData;
import com.hrms.model.vo.FileDataVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FileDataMapper {
    FileDataMapper INSTANCE = Mappers.getMapper(FileDataMapper.class);

    FileDataVO fileDataToFileDataVO(FileData fileData);
}
