package com.hrms.model.bo;

import com.hrms.enums.ShiftAdjustmentRequestApprovalStage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "班表調整審核資料")
public class ShiftAdjustmentApprovalBo {
    @Schema(description = "申請單 ID")
    private Long requestId;

    @Schema(description = "審核階段")
    private ShiftAdjustmentRequestApprovalStage stage;

    @Schema(description = "審核意見")
    private String remark;
}
