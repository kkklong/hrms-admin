package com.hrms.model.excel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Schema(description = "ShiftSchedulesExcel")
@Accessors(chain = true)
public class ShiftSchedulesExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "String", description = "部門名稱")
    private String departmentName;

    @Schema(type = "String", description = "員工別名")
    private String nickName;

    @Schema(type = "String", description = "班別色碼")
    private String shiftColorCodes;
}
