package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "新增角色")
public class CreateRoleBO {

    @NotBlank(message = "角色名稱不能為空")
    @Schema(type = "String",description = "人資,一般員工,主管,技術長,助理,老闆,其他")
    private String roleName;

    @Schema(type = "String",description = "可使用的菜單權限")
    private String menuPermission;
}
