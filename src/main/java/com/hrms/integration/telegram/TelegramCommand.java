package com.hrms.integration.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TelegramCommand {
    GET_GROUP_ID("/get_group_id", "取得當前群組 ID"),
    LINK_HRM("/link_hrm", "連結 HRMS"),
    JOIN("/join", "加入群組"),
    START("/start", "開始使用");

    private final String command;
    private final String description;

    public static TelegramCommand fromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        // Extract command part (ignore arguments and bot username suffix like @botname)
        String cmd = text.trim().split("\s+")[0].toLowerCase();
        if (cmd.contains("@")) {
            cmd = cmd.substring(0, cmd.indexOf("@"));
        }

        for (TelegramCommand c : values()) {
            if (c.command.equals(cmd)) {
                return c;
            }
        }
        return null;
    }
}
