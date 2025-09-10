package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 假單狀態
 */
@Getter
@AllArgsConstructor
public enum LeaveRecordStatus {
    SUBMITTED((byte) 0, "送審"),
    APPROVED((byte) 1, "批准"),
    REJECTED((byte) 2, "拒絕"),
    CANCELLED((byte) 3, "已銷假");

    private final Byte value;
    private final String name;
}
