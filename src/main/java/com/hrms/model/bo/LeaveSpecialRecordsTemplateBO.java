package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Schema(description = "LeaveSpecialRecordsTemplateBO")
@Accessors(chain = true)
public class LeaveSpecialRecordsTemplateBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "Integer", description = "ID")
    private Integer id;

    @NotBlank(message = "類型不能為空")
    @Schema(type = "String", description = "請假類型，如:事假、病假等")
    private String leaveTypes;

    @NotBlank(message = "類型不能為空")
    @Schema(type = "String", description = "計薪標準，包括：全薪、半薪、不計薪")
    private String salaryStandard;

    @Schema(type = "Boolean", description = "是否計算全勤獎金（0: 否，1: 是）")
    private Boolean fullAttendanceBonus;

    @NotNull
    @Schema(type = "Float", description = "最低請假單位")
    private Float minLeaveUnit;

    @NotNull
    @Schema(type = "Integer",description = "期間可請假天數上限")
    private Integer maxLeaveDays;

    @NotBlank(message = "類型不能為空")
    @Schema(type = "String", description = "計算期間類型（如：年度、從到職日開始等）0:曆年制 、1:週年制")
    private String calculationPeriod;

    @Schema(type = "Boolean", description = "是否要求連續請假 (0: 否, 1: 是)")
    private Boolean continuousLeave;

    @Schema(type = "Boolean", description = "是否需要提前提出請假申請 (0: 否, 1: 是)")
    private Boolean advanceApplication;

    @NotNull
    @Schema(type = "Float", description = "特別休假與選休假對應的欄位，所以特別休假日與選休假會有多筆資訊")
    private Float yearData;

    @Schema(type = "boolean", description = " 是否需要附件 (0: 否, 1: 是)")
    private Boolean attachmentRequired;
}
