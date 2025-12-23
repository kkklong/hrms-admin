package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Schema(description = "ShiftAdjustmentRequestVO")
@Accessors(chain = true)
public class ShiftAdjustmentRequestVO implements Serializable {


    @Schema(type = "Long", description = "申請單ID(自動生成)")
    private Long id;

    @Schema(type = "Integer", description = "申請人ID")
    private Integer applicantId;

    @Schema(type = "String", description = "申請人姓名")
    private String applicantName;

    @Schema(type = "Integer", description = "申請人部門ID")
    private Integer departmentId;

    @Schema(description = "變更內容 Map<ShiftId, NewShiftType> (申請人原班表ID, 目標班別)")
    private Map<Integer, String> shiftMap;

    @Schema(type = "String", description = "申請原因")
    private String reason;

    @Schema(type = "Integer", description = "狀態：0草稿,1審核中,2通過,3駁回,4取消")
    private Byte status;

    @Schema(type = "String", description = "審核流程快照")
    private String approvalContext;

    @Schema(type = "LocalDateTime", description = "申請時間")
    private LocalDateTime createdAt;

    @Schema(type = "String", description = "記錄每個階段的操作過程")
    private String historyReview;

    @Schema(type = "Boolean", description = "是否能被當前登入者審核")
    private Boolean eligibleForApproval;

    @Schema(description = "原始班別明細 Map<ShiftId, OriginalShiftType>")
    private Map<Integer, String> originalShiftMap;
}
