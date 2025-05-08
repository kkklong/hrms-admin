package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShiftScheduleStatus {

    /**
     * 上班及休假(含中間有請假，以及例休日、國定假日)
     */
    WORKING_AND_HOLIDAY((byte) 0, "上班及休假(例休日、國定假日)"),
    /**
     * 請整天假
     */
    LEAVE((byte) 1, "請假"),

    /**
     * 載入政府班表時預設的休假狀態(只在載入班表時判斷假日班別使用)
     */
    DEFAULT_HOLIDAY((byte) 2, "初始休假");

    private final Byte value;
    private final String name;

    public static ShiftScheduleStatus getByStringValue(String value) {
        for (ShiftScheduleStatus status : ShiftScheduleStatus.values()) {
            byte b = Byte.parseByte(value);
            if (status.getValue() == b) {
                return status;
            }
        }
        return null;
    }
}
