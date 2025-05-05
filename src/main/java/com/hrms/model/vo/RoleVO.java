package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Schema(description = "選單VO")
@Accessors(chain = true)
public class RoleVO implements Serializable {
    @Schema(type = "Integer",description = "角色ID")
    private Integer id;

    @Schema(type = "String",description = "人資,一般員工,主管,技術長,助理,老闆,其他")
    private String roleName;

    @Schema(type = "String",description = "可使用的菜單權限")
    private String menuPermission;
}
