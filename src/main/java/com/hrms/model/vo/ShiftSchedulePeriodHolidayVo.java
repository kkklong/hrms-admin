package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ShiftSchedulePeriodHolidayVo {

    @Schema(description = "假日日期")
    private LocalDate shiftDate;

    @Schema(description = "假日描述")
    private String remark;

    @Schema(description = "班別")
    private String shiftTypes;
}
