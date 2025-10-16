package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "EmployeeShiftSchedulesVO")
@Accessors(chain = true)
public class EmployeeShiftSchedulesVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "String", description = "班別，如：日班、夜班等")
    private String shiftTypes;

    @Schema(type = "LocalDate", description = "排班的具體日期")
    private LocalDate shiftDate;

    @Schema(type = "Byte", description = "排班狀態，0：上班，1：請假")
    private Byte status;

    @Schema(type = "String", description = "班別名稱")
    private String shiftName;


}
