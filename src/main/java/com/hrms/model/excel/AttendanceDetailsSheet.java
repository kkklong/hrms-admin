package com.hrms.model.excel;


import com.hrms.annotation.ExcelCell;
import com.hrms.enums.ExcelAggregationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "員工出勤紀錄excel by 考勤明細表")
@Accessors(chain = true)
public class AttendanceDetailsSheet implements Serializable {

    @Schema(description = "員工編號")
    private String employeeNumber;

    @ExcelCell(columnIndex = 0, title = "姓名", columnWidth = 10 * 256)
    @Schema(description = "員工全名")
    private String fullName;

    @ExcelCell(columnIndex = 1, title = "考勤日期", columnWidth = 10 * 256)
    @Schema(description = "考勤日期")
    private LocalDate attendanceDate;

    @ExcelCell(columnIndex = 2, title = "約定考勤上班時間", columnWidth = 20 * 256)
    @Schema(description = "約定考勤上班時間")
    private String agreementClockInTime;

    @ExcelCell(columnIndex = 3, title = "約定考勤下班時間", columnWidth =  20 * 256)
    @Schema(description = "約定考勤下班時間")
    private String agreementClockOutTime;

    @ExcelCell(columnIndex = 4, title = "是否彈性", columnWidth =  10 * 256)
    @Schema(description = "是否彈性")
    private String isFlexible;

    @ExcelCell(columnIndex = 5, title = "休息時間", columnWidth =  12 * 256)
    @Schema(description = "休息時間")
    private String restTime;

    @ExcelCell(columnIndex = 6, title = "實際考勤上班時間", columnWidth = 20 * 256)
    @Schema(description = "上班")
    private String clockInTime;

    @ExcelCell(columnIndex = 7, title = "實際考勤下班時間", columnWidth =  20 * 256)
    @Schema(description = "下班")
    private String clockOutTime;

    @ExcelCell(columnIndex = 8, title = "狀態", columnWidth =  5 * 256)
    @Schema(description = "0正常，1異常，2請假，3其他")
    private String status;

    @ExcelCell(columnIndex = 9, title = "遲到分鐘數", columnWidth =  13 * 256, aggregationType = ExcelAggregationType.SUM)
    @Schema(description = "遲到分鐘數")
    private Integer lateMinutes;

    @ExcelCell(columnIndex = 10, title = "早退分鐘數", columnWidth =  13 * 256, aggregationType = ExcelAggregationType.SUM)
    @Schema(description = "早退分鐘數")
    private Integer earlyLeaveMinutes;

    @ExcelCell(columnIndex = 11, title = "曠工分鐘數", columnWidth =  13 * 256, aggregationType = ExcelAggregationType.SUM)
    @Schema(description = "曠工分鐘數")
    private Integer absenteeismMinutes;

}
