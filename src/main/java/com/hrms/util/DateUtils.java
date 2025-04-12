package com.hrms.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class DateUtils {

    /**
     * YYYY-MM-DD 格式，允許 2 月有 29 天，不考慮閏年
     */
    public static final String YYYY_MM_DD_DASH =
            "(\\d{4})-(?:" +
                    "(?:[13578]|0[13578]|1[02])-(?:[1-9]|0[1-9]|[12]\\d|3[01])|" +  // 1, 3, 5, 7, 8, 10, 12 月有 31 天
                    "(?:[469]|0[469]|11)-(?:[1-9]|0[1-9]|[12]\\d|30)|" +           // 4, 6, 9, 11 月有 30 天
                    "(?:2|02)-(?:[1-9]|0[1-9]|1\\d|2[0-9])" +                      // 2 月最多有 29 天
                    ")";

    /**
     * YYYY/MM/DD 格式，允許 2 月有 29 天，不考慮閏年
     */
    public static final String YYYY_MM_DD_SLASH =
            "(\\d{4})/(?:" +
                    "(?:[13578]|0[13578]|1[02])/(?:[1-9]|0[1-9]|[12]\\d|3[01])|" +  // 1, 3, 5, 7, 8, 10, 12 月有 31 天
                    "(?:[469]|0[469]|11)/(?:[1-9]|0[1-9]|[12]\\d|30)|" +           // 4, 6, 9, 11 月有 30 天
                    "(?:2|02)/(?:[1-9]|0[1-9]|1\\d|2[0-9])" +                      // 2 月最多有 29 天
                    ")";

    /**
     * HH:mm:ss 格式，24 小時制
     */
    public static final String HH_MM_SS =
            "(?:[01]?\\d|2[0-3]):[0-5]\\d:[0-5]\\d";  // 時:分:秒，24 小時制

    /**
     * HH:mm 格式，24 小時制
     */
    public static final String HH_MM =
            "(?:[01]?\\d|2[0-3]):[0-5]\\d";  // 時:分，24 小時制

    @Getter
    public enum DatePattern {
        NONE(null, null, false),
        YYYY_MM_DD_DASH(DateUtils.YYYY_MM_DD_DASH, DateTimeFormatter.ofPattern("yyyy-M-d"), false),
        YYYY_MM_DD_SLASH(DateUtils.YYYY_MM_DD_SLASH, DateTimeFormatter.ofPattern("yyyy/M/d"), false),
        YYYY_MM_DD_HH_MM_SS_DASH(DateUtils.YYYY_MM_DD_DASH + " " + DateUtils.HH_MM_SS, DateTimeFormatter.ofPattern("yyyy-M-d H:m:s"), true),
        YYYY_MM_DD_HH_MM_SS_SLASH(DateUtils.YYYY_MM_DD_SLASH + " " + DateUtils.HH_MM_SS, DateTimeFormatter.ofPattern("yyyy/M/d H:m:s"), true),
        YYYY_MM_DD_HH_MM_DASH(DateUtils.YYYY_MM_DD_DASH + " " + DateUtils.HH_MM, DateTimeFormatter.ofPattern("yyyy-M-d H:m"), true),
        YYYY_MM_DD_HH_MM_SLASH(DateUtils.YYYY_MM_DD_SLASH + " " + DateUtils.HH_MM, DateTimeFormatter.ofPattern("yyyy/M/d H:m"), true),
        YYYY_MM_DD_T_HH_MM_SS_DASH(DateUtils.YYYY_MM_DD_DASH + "T" + DateUtils.HH_MM_SS, DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s"), true),
        YYYY_MM_DD_T_HH_MM_SS_SLASH(DateUtils.YYYY_MM_DD_SLASH + "T" + DateUtils.HH_MM_SS, DateTimeFormatter.ofPattern("yyyy/M/d'T'H:m:s"), true),
        YYYY_MM_DD_T_HH_MM_DASH(DateUtils.YYYY_MM_DD_DASH + "T" + DateUtils.HH_MM, DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m"), true),
        YYYY_MM_DD_T_HH_MM_SLASH(DateUtils.YYYY_MM_DD_SLASH + "T" + DateUtils.HH_MM, DateTimeFormatter.ofPattern("yyyy/M/d'T'H:m"), true),
        // 可以根據需求增加其他格式
        ;

        private final String regex;
        private final DateTimeFormatter formatter;
        private final boolean isDateTime;

        DatePattern(String regex, DateTimeFormatter formatter, boolean isDateTime) {
            this.regex = "^" + regex + "$";  // 在建構式中動態添加 ^ 和 $
            this.formatter = formatter;
            this.isDateTime = isDateTime;
        }
    }

    public static LocalDate parseToLocalDate(String dateString) {
        LocalDateTime localDateTime = parseToLocalDateTime(dateString);
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toLocalDate();
    }

    public static LocalDateTime parseToLocalDateTime(String dateString) {
        for (DatePattern datePattern : DatePattern.values()) {
            if (dateString.matches(datePattern.getRegex())) {
                try {
                    if (datePattern.isDateTime()) {
                        return LocalDateTime.parse(dateString, datePattern.getFormatter());
                    } else {
                        LocalDate date = LocalDate.parse(dateString, datePattern.getFormatter());
                        return date.atTime(LocalTime.MIN);
                    }
                } catch (DateTimeParseException e) {
                    throw new UnsupportedOperationException(
                            String.format("LocalDateTime 日期格式解析失敗：%s，format：%s regex:%s"
                                    , dateString, datePattern, datePattern.getRegex()));
                }
            }
        }
        throw new UnsupportedOperationException(String.format("LocalDateTime 日期格式解析失敗：%s，不存在此格式", dateString));
    }

}

