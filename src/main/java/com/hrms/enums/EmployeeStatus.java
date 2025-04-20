package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 員工在職狀態
 */
@Getter
@AllArgsConstructor
public enum EmployeeStatus {
    RESIGNED((byte) 0, "離職"),
    ACTIVE((byte) 1, "在職"),
    RETAINED((byte) 2, "留職"),
    OTHER((byte) 3, "其他");

    private final Byte value;
    private final String name;
}
