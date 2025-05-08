package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 班別時段
 */
@Getter
@AllArgsConstructor
public enum TimeSlot {
    MORNING("morning", "早上時段"),
    AFTERNOON("afternoon", "中午時段"),
    NIGHT("night", "晚上時段");

    private final String value;
    private final String name;
}


