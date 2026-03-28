package com.hrms.model.vo;

import com.hrms.annotation.ExcelCell;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "稽查紀錄VO")
@Accessors(chain = true)
public class RemoteAuditRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "Integer", description = "員工ID")
    private Integer employeeId;

    @ExcelCell(columnIndex = 0, title = "員工暱稱", columnWidth = 10 * 256)
    @Schema(type = "string", description = "員工姓名")
    private String employeeName;

    @ExcelCell(columnIndex = 1, title = "稽查發送時間", columnWidth = 10 * 256)
    @Schema(type = "LocalDateTime", description = "發送時間")
    private LocalDateTime sentAt;

    @ExcelCell(columnIndex = 2, title = "稽查截止時間", columnWidth = 20 * 256)
    @Schema(type = "LocalDateTime", description = "截止時間")
    private LocalDateTime deadlineAt;

    @ExcelCell(columnIndex = 3, title = "回覆時間", columnWidth =  20 * 256)
    @Schema(type = "LocalDateTime", description = "回覆時間")
    private LocalDateTime repliedAt;

    @Schema(type = "Integer", description = "狀態碼")
    private Integer status;

    @ExcelCell(columnIndex = 4, title = "狀態描述", columnWidth =  10 * 256)
    @Schema(type = "string", description = "狀態描述")
    private String statusText;


}
