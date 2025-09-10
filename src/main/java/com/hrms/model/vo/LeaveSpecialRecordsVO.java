package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Schema(description = "LeaveSpecialRecordsVO")
@Accessors(chain = true)
public class LeaveSpecialRecordsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "Integer", description = "ID(自動生成)")
    private Integer id;

    @Schema(type = "Integer",description = "員工ID")
    private Integer employeeId;

    @Schema(type = "String",description = "員工別名")
    private String nickName;

    @Schema(type = "Integer", description = "部⻔ID")
    private Integer departmentId;

    @Schema(type = "String", description = "部⻔名稱")
    private String departmentName;

    @Schema(type = "String", description = "請假類型，如:事假、病假等")
    private String leaveTypes;

    @Schema(type = "LocalDateTime", description = "生效時間")
    private LocalDateTime startDate;

    @Schema(type = "LocalDateTime", description = "失效時間")
    private LocalDateTime endDate;

    @Schema(type = "String", description = "假別中文名稱")
    private String chineseName;

    @Schema(type = "String", description = "計薪標準，包括：全薪、半薪、不計薪")
    private String salaryStandard;

    @Schema(type = "boolean", description = "是否計算全勤獎金（0: 否，1: 是）")
    private Boolean fullAttendanceBonus;

    @Schema(type = "Float", description = "最低請假單位")
    private Float minLeaveUnit;

    @Schema(type = "Integer",description = "期間可請假天數上限")
    private Integer maxLeaveDays;

    @Schema(type = "boolean", description = "是否要求連續請假 (0: 否, 1: 是)")
    private Boolean continuousLeave;

    @Schema(type = "boolean", description = "是否需要提前提出請假申請 (0: 否, 1: 是)")
    private Boolean advanceApplication;

    @Schema(type = "String", description = "說明 (僅限於說明，不參与流程限制)")
    private String description;

    @Schema(type = "String", description = "特休結算現金年月")
    private String settlementDate;

    @Schema(type = "Float", description = "特休結算現金時數")
    private Float settlementCount;

    @Schema(type = "boolean", description = " 是否需要附件 (0: 否, 1: 是)")
    private Boolean attachmentRequired;
}
