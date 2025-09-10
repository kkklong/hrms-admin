package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 計算期間類型
 */
@Getter
@AllArgsConstructor
public enum LeaveSpecialRecordCalculationPeriod {
    CALENDAR_YEAR("0", "曆年制"),
    ANNIVERSARY_YEAR("1", "週年制");

    private final String value;
    private final String name;
}
