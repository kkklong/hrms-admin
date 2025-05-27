package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 計算期間類型
 */
@Getter
@AllArgsConstructor
public enum NoticeType {
    GENERAL_NOTICE("一般通知"),
    WORKING_RULE("工作規則"),
    URGENT_NOTICE("緊急通知"),
    EVENT_ANNOUNCEMENT("活動公告");

    private final String value;
}
