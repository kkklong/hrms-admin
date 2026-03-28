package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Schema(description = "出勤記錄BO")
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRecordsBO {
    @Schema(description = "員工ID")
    Integer employeeId;

    @NotNull
    @Schema(description = "開始時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate;

    @NotNull
    @Schema(description = "結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate endDate;
}
