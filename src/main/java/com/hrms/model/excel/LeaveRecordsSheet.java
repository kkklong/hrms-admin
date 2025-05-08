package com.hrms.model.excel;


import com.hrms.annotation.ExcelCell;
import com.hrms.enums.ExcelAggregationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "員工出勤紀錄excel by 請假加班")
@Accessors(chain = true)
public class LeaveRecordsSheet implements Serializable {

    @Schema(description = "員工編號")
    private String employeeNumber;

    @ExcelCell(columnIndex = 0, title = "姓名", columnWidth = 10 * 256)
    @Schema(description = "員工全名")
    private String fullName;

    @ExcelCell(columnIndex = 1, title = "日期", columnWidth = 10 * 256)
    @Schema(description = "考勤日期")
    private LocalDate date;

    @ExcelCell(columnIndex = 2, title = "開始時間", columnWidth = 20 * 256)
    @Schema(description = "開始時間")
    private String startDateTime;

    @ExcelCell(columnIndex = 3, title = "結束時間", columnWidth =  20 * 256)
    @Schema(description = "結束時間")
    private String endDateTime;

    @ExcelCell(columnIndex = 4, title = "時數", columnWidth =  10 * 256, aggregationType = ExcelAggregationType.SUM)
    @Schema(description = "時數")
    private Float countVal;

    @ExcelCell(columnIndex = 5, title = "類型", columnWidth =  13 * 256)
    @Schema(description = "類型")
    private String type;

    @ExcelCell(columnIndex = 6, title = "假別", columnWidth =  15 * 256)
    @Schema(description = "假別")
    private String leaveTypeName;

    @Schema(description = "假別類型")
    private String leaveTypes;

}
