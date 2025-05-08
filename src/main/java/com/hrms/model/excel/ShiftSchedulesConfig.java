package com.hrms.model.excel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Schema(description = "ShiftSchedulesConfig")
@Accessors(chain = true)
public class ShiftSchedulesConfig implements Serializable {

    private static final long serialVersionUID = 1L;


    @Schema(type = "String", description = "部門名稱")
    private String departmentName;

    @Schema(type = "String", description = "班別，如：日班、夜班等")
    private String configName;

    @Schema(type = "String", description = "班別色碼")
    private String shiftColorCode;
}
