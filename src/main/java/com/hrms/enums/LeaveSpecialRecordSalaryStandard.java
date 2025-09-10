package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 記薪標準
 */
@Getter
@AllArgsConstructor
public enum LeaveSpecialRecordSalaryStandard {
    FULL_PAY("0", "全薪"),
    HALF_PAY("1", "半薪"),
    NO_PAY("2", "不計薪");

    private final String value;
    private final String name;
}
