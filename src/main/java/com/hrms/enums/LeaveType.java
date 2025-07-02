package com.hrms.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum LeaveType {
    ANNUAL_LEAVE("0", "特休假", "Annual Leave", true),
    OPTIONAL_LEAVE("1", "選休假", "Optional Leave", true),
    PERSONAL_LEAVE("2", "事假", "Personal Leave", true),
    SICK_LEAVE("3", "普通傷病假", "Sick Leave", true),
    PAID_SICK_LEAVE("4", "有薪病假", "Paid Sick Leave", true),
    MENSTRUAL_LEAVE("5", "生理病假", "Menstrual Sick Leave", true),
    MENSTRUAL_SICK_LEAVE("6", "生理假", "Menstrual Leave", true),
    FAMILY_CARE_LEAVE("7", "家庭照顧假", "Family Care Leave", true),
    MARRIAGE_LEAVE("8", "婚假", "Marriage Leave", false),
    FUNERAL_LEAVE_1("9", "喪假（父母、養父母、繼父母、配偶）", "Funeral Leave 1", false),
    FUNERAL_LEAVE_2("10", "喪假（祖父母、外公外婆、子女、配偶的父母）", "Funeral Leave 2", false),
    FUNERAL_LEAVE_3("11", "喪假（曾祖父母、兄弟姐妹、配偶的曾祖父母）", "Funeral Leave 3", false),
    INJURY_LEAVE("12", "公傷病假", "Injury Leave", false),
    PUBLIC_LEAVE("13", "公假", "Public Leave", false),
    BUSINESS_TRIP_LEAVE("14", "公出假", "Business Trip Leave", false),
    MATERNITY_LEAVE("15", "產假", "Maternity Leave", false),
    ANTE_NATAL_LEAVE("16", "安胎假", "Antenatal Leave", false),
    PATERNITY_LEAVE("17", "陪產假", "Paternity Leave", false),
    PRENATAL_CHECKUP_LEAVE("18", "產檢假", "Prenatal Checkup Leave", false),
    COMPENSATORY_LEAVE("19", "補休", "Compensatory Leave", false),
    OTHER("20", "其他", "Other", false);

    private static final Map<String, LeaveType> LEAVE_TYPE_MAP = new HashMap<>();

    static {
        for (LeaveType type : LeaveType.values())
            LEAVE_TYPE_MAP.put(type.getLeaveType(), type);
    }

    private final String leaveType;
    private final String chineseName;
    private final String englishName;
    private final boolean isAutoScheduled;

    LeaveType(String leaveType, String chineseName, String englishName, boolean isAutoScheduled) {
        this.leaveType = leaveType;
        this.chineseName = chineseName;
        this.englishName = englishName;
        this.isAutoScheduled = isAutoScheduled;
    }

    public static String getChineseNameByLeaveType(String leaveType) {
        LeaveType type = LEAVE_TYPE_MAP.get(leaveType);
        return type != null ? type.getChineseName() : null;
    }


}
