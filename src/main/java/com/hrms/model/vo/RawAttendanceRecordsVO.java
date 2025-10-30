package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RawAttendanceRecordsVO {
    @Schema(description = "員工帳號")
    private String account;
    @Schema(description = "首次打卡時間")
    private LocalDateTime firstCheckInTime;
    @Schema(description = "最後打卡時間")
    private LocalDateTime lastCheckOutTime;
}
