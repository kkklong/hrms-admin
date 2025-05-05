package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 員工所屬公司
 */
@Getter
@AllArgsConstructor
public enum CompanyType {
    COMMON((byte) 0, "共通"),
    TG((byte) 1, "TG"),
    EG((byte) 2, "EG");

    private final Byte value;
    private final String name;
}
