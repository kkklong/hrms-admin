package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "ShiftSchedulesVO")
@Accessors(chain = true)
public class ShiftSchedulesVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "Integer", description = "排班ID(自動生成)")
    private Integer id;

    @Schema(type = "Integer", description = "員工ID")
    private Integer employeeId;

    @Schema(type = "String", description = "員工別名")
    private String nickName;

    @Schema(type = "Integer", description = "部門ID")
    private Integer departmentId;

    @Schema(type = "String", description = "部門名稱")
    private String departmentName;

    @Schema(type = "String", description = "班別，如：日班、夜班等")
    private String shiftTypes;

    @Schema(type = "LocalDate", description = "排班的具體日期")
    private LocalDate shiftDate;

    @Schema(type = "Byte", description = "排班狀態，0：上班，1：請假")
    private Byte status;

    @Schema(type = "Byte", description = "周別類型，1 表示第1周，2 表示第2周")
    private Byte weekType;

    @Schema(type = "String", description = "備註，記錄額外信息，如特殊說明等")
    private String remark;

    @Schema(type = "Byte", description = "班別修改狀態 0: 可修改、1: 不可修改")
    private Byte actionType;

    @Schema(type = "String", description = "班別色碼")
    private String shiftColorCode;
}
