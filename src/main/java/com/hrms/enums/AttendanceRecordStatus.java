package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceRecordStatus {
    NORMAL(0, "正常"),
    ABNORMAL(1, "異常"),
    LEAVE(2, "請假"),
    ABSENT(3, "其他");
    private final int value;
    private final String name;
}
