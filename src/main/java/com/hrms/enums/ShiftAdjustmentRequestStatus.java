package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShiftAdjustmentRequestStatus {
    DRAFT((byte) 0, "草稿"),
    SUBMITTED((byte) 1, "送審"),
    APPROVED((byte) 2, "批准"),
    REJECTED((byte) 3, "拒絕"),
    CANCELLED((byte) 4, "取消");

    private final Byte value;
    private final String name;
}

