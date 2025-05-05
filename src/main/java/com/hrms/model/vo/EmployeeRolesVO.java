package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "角色權限")
@Accessors(chain = true)
public class EmployeeRolesVO {

    @Schema(type = "Integer", description = "員工ID")
    private Integer employeeId;

    @Schema(type = "String", description = "帳號")
    private String account;

    @Schema(type = "String", description = "全名")
    private String fullName;

    @Schema(type = "Integer", description = "角色ID")
    private Integer roleId;

    @Schema(type = "String", description = "角色名稱")
    private String roleName;

}
