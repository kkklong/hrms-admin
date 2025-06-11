package com.hrms.entity.mapstruct;

import com.hrms.entity.Menu;
import com.hrms.model.vo.MenuRolesVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface MenuMapper {
    MenuMapper INSTANCE = Mappers.getMapper(MenuMapper.class);

    MenuRolesVO toMenuRolesVO(Menu menu);

}
