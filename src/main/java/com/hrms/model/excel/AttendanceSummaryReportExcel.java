package com.hrms.model.excel;

import com.hrms.annotation.ExcelCell;
import com.hrms.constant.ExcelConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AttendanceSummaryReportExcel {

    @ExcelCell(columnIndex = 0, title = "員工全名", columnWidth = 10 * 256)
    @Schema(description = "員工全名")
    private String fullName;

    @ExcelCell(columnIndex = 1, title = "部門", columnWidth = 5 * 256)
    @Schema(description = "部門")
    private String departmentName;

    @ExcelCell(columnIndex = 2, title = "員工編號", columnWidth = 10 * 256)
    @Schema(description = "員工編號")
    private String employeeNumber;

    @ExcelCell(columnIndex = 3, title = "總工作天數", columnWidth = 12 * 256)
    @Schema(description = "總工作天數")
    private Integer totalWorkDay;

    @ExcelCell(columnIndex = 4, title = "實際出勤天數", columnWidth = 14 * 256)
    @Schema(description = "實際出勤天數")
    private Integer totalPresentDay;

    @ExcelCell(columnIndex = 5, title = "遲到總分鐘數", columnWidth = 14 * 256)
    @Schema(description = "遲到總分鐘數")
    private Integer totalLateMinute;

    @ExcelCell(columnIndex = 6, title = "早退總分鐘數", columnWidth = 14 * 256)
    @Schema(description = "早退總分鐘數")
    private Integer totalEarlyLeaveMinute;

    @ExcelCell(columnIndex = 7, title = "曠工總分鐘數", columnWidth = 14 * 256)
    @Schema(description = "曠工總分鐘數")
    private Integer totalAbsenteeismMinute;

    @ExcelCell(columnIndex = 8, title = "年節獎金", columnWidth = 10 * 256)
    @Schema(description = "年節獎金")
    private Integer bonus;

    @ExcelCell(columnIndex = 9, title = "生日禮金", columnWidth = 10 * 256)
    @Schema(description = "生日禮金")
    private Integer birthdayBonus;

    @ExcelCell(columnIndex = 10, title = "全勤津貼", columnWidth = 14 * 256)
    @Schema(description = "全勤津貼")
    private Integer fullAttendanceBonus;

    @ExcelCell(columnIndex = 11, title = "有薪請假的總時數", columnWidth = 20 * 256)
    @Schema(description = "有薪請假的總時數")
    private Float totalPaidLeaveHour;

    @ExcelCell(columnIndex = 12, title = "半薪請假的總時數", columnWidth = 20 * 256)
    @Schema(description = "半薪請假的總時數")
    private Float totalHalfPaidLeaveHour;

    @ExcelCell(columnIndex = 13, title = "不計薪請假的總時數", columnWidth = 22 * 256)
    @Schema(description = "不計薪請假的總時數")
    private Float totalUnpaidLeaveHour;

    @ExcelCell(columnIndex = 14, title = "早班的總天數", columnWidth = 14 * 256)
    @Schema(description = "早班的總天數")
    private Float totalMorningShiftDay;

    @ExcelCell(columnIndex = 15, title = "假日津貼", columnWidth = 10 * 256)
    @Schema(description = "假日津貼")
    private Float holidayDutyAllowance;

    @ExcelCell(columnIndex = 16, title = "假日班的總天數", columnWidth = 16 * 256)
    @Schema(description = "假日班的總天數")
    private Integer totalHolidayShiftDay;

    @ExcelCell(columnIndex = 17, title = "午班津貼", columnWidth = 10 * 256)
    @Schema(description = "午班津貼")
    private Integer afternoonShiftAllowance;

    @ExcelCell(columnIndex = 18, title = "午班的總天數", columnWidth = 14 * 256)
    @Schema(description = "午班的總天數")
    private Integer totalAfternoonShiftDay;

    @ExcelCell(columnIndex = 19, title = "晚班津貼", columnWidth = 10 * 256)
    @Schema(description = "晚班津貼")
    private Integer nightShiftAllowance;

    @ExcelCell(columnIndex = 20, title = "晚班的總天數", columnWidth = 14 * 256)
    @Schema(description = "晚班的總天數")
    private Integer totalNightShiftDay;

    @ExcelCell(columnIndex = 21, title = "夜班津貼(月)", columnWidth = 14 * 256)
    @Schema(description = "夜班津貼(月)")
    private Integer fullNightShiftAllowance;

    @ExcelCell(columnIndex = 22, title = "發放總津貼", columnWidth = 12 * 256, cellFormula = ExcelConstants.BONUS_TOTAL, cellFormulaRowBase = 0)
    @Schema(description = "發放總津貼")
    private Integer bonusTotal;

    @ExcelCell(columnIndex = 23, title = "加班換現金", columnWidth = 12 * 256, cellFormula = ExcelConstants.ATTENDANCE_SUMMARY_OVERTIME_CONVERTED_TO_CASH, cellFormulaRowBase = 0)
    @Schema(description = "加班換現金")
    private Integer overtimeConvertedToCash;

    @ExcelCell(columnIndex = 24, title = "前 2 小時加班時數", columnWidth = 24 * 256)
    @Schema(description = "前 2 小時加班時數")
    private Float totalFirst2hMultiplier;

    @ExcelCell(columnIndex = 25, title = "超過 2 小時的加班時數", columnWidth = 24 * 256)
    @Schema(description = "超過 2 小時的加班時數")
    private Float totalBeyond2hMultiplier;

    @ExcelCell(columnIndex = 26, title = "加班換補休學時數", columnWidth = 20 * 256)
    @Schema(description = "加班換補休學時數")
    private Float totalCompensatoryLeaveHour;

    @ExcelCell(columnIndex = 27, title = "到期特休換現金時數", columnWidth = 22 * 256)
    @Schema(description = "到期特休換現金時數")
    private Float expiredSpecialLeaveCashHour;

    @ExcelCell(columnIndex = 28, title = "勞保費用", columnWidth = 10 * 256)
    @Schema(description = "勞保費用")
    private Integer laborInsuranceFee;

    @ExcelCell(columnIndex = 29, title = "健保費用", columnWidth = 10 * 256)
    @Schema(description = "健保費用")
    private Integer healthInsuranceFee;

    @ExcelCell(columnIndex = 30, title = "伙食津貼", columnWidth = 10 * 256)
    @Schema(description = "伙食津貼")
    private Integer attendanceSummaryReport;

    @ExcelCell(columnIndex = 31, title = "勞退自提%數", columnWidth = 13 * 256)
    @Schema(description = "勞退自提%數")
    private Integer voluntaryPensionContribution;

    @ExcelCell(columnIndex = 32, title = "勞退自額", columnWidth = 10 * 256, cellFormula = ExcelConstants.ATTENDANCE_SUMMARY_VOLUNTARY_PENSION, cellFormulaRowBase = 0)
    @Schema(description = "勞退自額")
    private Integer voluntaryPension;

    @ExcelCell(columnIndex = 33, title = "投保眷口數", columnWidth = 12 * 256)
    @Schema(description = "投保眷口數")
    private Integer insuredDependentsCount;

    @ExcelCell(columnIndex = 34, title = "代扣稅款", columnWidth = 10 * 256)
    @Schema(description = "代扣稅款")
    private Integer withholdingTax;

    @ExcelCell(columnIndex = 35, title = "公司付擔勞保費用", columnWidth = 20 * 256)
    @Schema(description = "公司付擔勞保費用")
    private Integer companyLaborInsuranceFee;

    @ExcelCell(columnIndex = 36, title = "公司付擔健保費用", columnWidth = 20 * 256)
    @Schema(description = "公司付擔健保費用")
    private Integer companyHealthInsuranceFee;

    @ExcelCell(columnIndex = 37, title = "含伙食薪資", columnWidth = 12 * 256)
    @Schema(description = "含伙食薪資")
    private Integer salary;

    @ExcelCell(columnIndex = 38, title = "總發薪資", columnWidth = 10 * 256, cellFormula = ExcelConstants.ATTENDANCE_SUMMARY_SALARY_TOTAL, cellFormulaRowBase = 0)
    @Schema(description = "總發薪資")
    private Integer salaryTotal;
}
