package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "ShiftSchedulesBO")
@Accessors(chain = true)
public class ShiftSchedulesBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Schema(type = "Integer", description = "排班ID(自動生成)")
    private Integer id;

    @NotNull
    @Schema(type = "Integer", description = "員工ID")
    private Integer employeeId;

    @NotNull
    @Schema(type = "LocalDate", description = "排班的具體日期，如2024-08-29")
    private LocalDate shiftDate;

    @NotBlank(message = "班別不能為空")
    @Schema(type = "String", description = "班別")
    private String shiftTypes;

    @Schema(type = "String", description = "備註，記錄額外信息，如特殊說明等")
    private String remark;

}
