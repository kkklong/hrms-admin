package com.hrms.enums;

public enum AlertEventType {
    CLOCK_IN_MISS(1);  // 上班未打卡

    private final int code;
    AlertEventType(int code) { this.code = code; }
    public int getCode() { return code; }
}