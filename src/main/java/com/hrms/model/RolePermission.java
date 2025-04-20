package com.hrms.model;

import lombok.Data;

@Data
public class RolePermission {
    private String account;
    private Integer roleId;
    private String roleName;
    private String menuPermission;
}
