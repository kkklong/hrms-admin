package com.hrms.model.excel;

import com.hrms.annotation.ExcelCell;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "打卡紀錄導入Bean")
@Accessors(chain = true)
public class AttendanceRecordsBean implements Serializable {

    @ExcelCell(columnIndex = 0)
    @Schema(description = "員工帳號")
    private String account;

    @ExcelCell(columnIndex = 1)
    @Schema(description = "考期日期")
    private LocalDate attendanceDate;

    @ExcelCell(columnIndex = 2)
    @Schema(description = "上班")
    private LocalDateTime clockInTime;

    @ExcelCell(columnIndex = 3)
    @Schema(description = "下班")
    private LocalDateTime clockOutTime;

}
