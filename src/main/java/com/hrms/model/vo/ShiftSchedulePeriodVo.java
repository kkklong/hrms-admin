package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftSchedulePeriodVo {

    @Schema(description = "區間起始日期")
    private LocalDate startDate;

    @Schema(description = "區間結束日期")
    private LocalDate endDate;

    @Schema(description = "區間內假日")
    private List<ShiftSchedulePeriodHolidayVo> holidays;
}
