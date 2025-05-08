package com.hrms.model.excel;


import com.hrms.annotation.ExcelCell;
import com.hrms.enums.ExcelAggregationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "出勤紀錄excel")
@Accessors(chain = true)
public class AttendanceRecordsExcel implements Serializable {

    @ExcelCell(columnIndex = 0, title = "員工暱稱", columnWidth = 10 * 256)
    @Schema(description = "員工暱稱")
    private String nickName;

    @ExcelCell(columnIndex = 1, title = "考勤日期", columnWidth = 10 * 256)
    @Schema(description = "考勤日期")
    private LocalDate attendanceDate;

    @ExcelCell(columnIndex = 2, title = "上班", columnWidth = 20 * 256)
    @Schema(description = "上班")
    private LocalDateTime clockInTime;

    @ExcelCell(columnIndex = 3, title = "下班", columnWidth =  20 * 256)
    @Schema(description = "下班")
    private LocalDateTime clockOutTime;

    @ExcelCell(columnIndex = 4, title = "班別狀態", columnWidth =  10 * 256)
    @Schema(description = "0正常，1異常，2請假，3其他")
    private Integer status;

    @ExcelCell(columnIndex = 5, title = "遲到分鐘數", columnWidth =  13 * 256, aggregationType = ExcelAggregationType.SUM)
    @Schema(description = "遲到分鐘數")
    private Integer lateMinutes;

    @ExcelCell(columnIndex = 6, title = "早退分鐘數", columnWidth =  13 * 256, aggregationType = ExcelAggregationType.SUM)
    @Schema(description = "早退分鐘數")
    private Integer earlyLeaveMinutes;

    @ExcelCell(columnIndex = 7, title = "曠工分鐘數", columnWidth =  13 * 256, aggregationType = ExcelAggregationType.SUM)
    @Schema(description = "曠工分鐘數")
    private Integer absenteeismMinutes;

}
