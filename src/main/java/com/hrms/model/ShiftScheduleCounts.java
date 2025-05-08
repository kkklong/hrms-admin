package com.hrms.model;

import lombok.Data;

@Data
public class ShiftScheduleCounts {

    /**
     * 早班人數
     */
    private int morningCount;

    /**
     * 午班人數
     */
    private int afternoonCount;

    /**
     * 夜班人數
     */
    private int nightCount;
}