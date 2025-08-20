package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 審核流程ScopeType
 */
@Getter
@AllArgsConstructor
public enum ScopeType {
    EMPLOYEE("員工"),
    DEPARTMENT("部門"),
    COMPANY("公司"),
    GLOBAL("全局");


    private final String name;

}
