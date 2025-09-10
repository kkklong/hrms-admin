package com.hrms.model.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Schema(description = "LeaveSpecialRecordsBO")
@Accessors(chain = true)
public class LeaveSpecialRecordsBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "Integer", description = "ID(自動生成)")
    private Integer id;

    @NotNull
    @Schema(type = "Integer",description = "員工ID")
    private Integer employeeId;

    @NotBlank(message = "類型不能為空")
    @Schema(type = "String", description = "請假類型，如:事假、病假等")
    private String leaveTypes;

    @NotNull
    @Schema(type = "LocalDateTime", description = "生效時間")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime startDate;

    @NotNull
    @Schema(type = "LocalDateTime", description = "失效時間")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime endDate;

    @NotBlank(message = "類型不能為空")
    @Schema(type = "String", description = "計薪標準，包括：全薪、半薪、不計薪")
    private String salaryStandard;

    @NotNull(message = "是否計算全勤不能為空")
    @Schema(type = "boolean", description = "是否計算全勤獎金（0: 否，1: 是）")
    private Boolean fullAttendanceBonus;

    @NotNull
    @Schema(type = "Float", description = "最低請假單位")
    private Float minLeaveUnit;

    @NotNull
    @Schema(type = "Integer",description = "期間可請假天數上限")
    private Integer maxLeaveDays;

    @NotNull(message = "是否需要連續請假不能為空")
    @Schema(description = "是否要求連續請假 (0: 否, 1: 是)")
    private Boolean continuousLeave;

    @NotNull(message = "是否需要事前提出不能為空")
    @Schema(description = "是否需要提前提出請假申請 (0: 否, 1: 是)")
    private Boolean advanceApplication;

    @Schema(type = "String", description = "說明 (僅限於說明，不參与流程限制)")
    private String description;

    @Schema(type = "String", description = "特休結算現金年月")
    @JsonFormat(pattern = "yyyy-MM")
    private String settlementDate;

    @Schema(type = "Float", description = "特休結算現金時數")
    private Float settlementCount;

    @NotNull(message = "是否需要證明檔案不能為空")
    @Schema(type = "boolean", description = " 是否需要附件 (0: 否, 1: 是)")
    private Boolean attachmentRequired;
}
