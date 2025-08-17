package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 請假區間審核
 */
@Getter
@AllArgsConstructor
public enum ReviewInterval {
    GE_24("大於等於24小時"),
    LT_24("小於24小時");

    private final String name;

}
