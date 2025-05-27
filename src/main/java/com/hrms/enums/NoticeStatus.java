package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公告狀態
 */
@Getter
@AllArgsConstructor
public enum NoticeStatus {
    UNPUBLISHED((byte) 0, "未發佈"),
    PUBLISHED((byte) 1, "已發佈"),
    REVOKED((byte) 2, "已撤銷");

    private final Byte value;
    private final String name;
}
