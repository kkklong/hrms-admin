package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "遠端打卡項目 BO")
@AllArgsConstructor
@NoArgsConstructor
public class RemoteAttendancePeriodDataBO {

    @Schema(description = "遠端打卡ID(自動生成)")
    private Integer id;

    @NotNull
    @Schema(description = "遠端允許日期時間，格式 yyyy/MM/dd")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate remoteDate;


    @Schema(description = "允許班別(複選)")
    private List<String> availableShiftType;
}