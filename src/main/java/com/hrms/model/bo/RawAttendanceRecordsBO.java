package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(description = "打卡記錄BO")
public class RawAttendanceRecordsBO {
    @Schema(description = "員工帳號")
    String account;

    @Schema(description = "開始時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    LocalDateTime startDate;

    @Schema(description = "結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    LocalDateTime endDate;

    @Schema(description = "是否顯示詳細記錄")
    Boolean showDetail;
}
