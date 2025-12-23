package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "ShiftAdjustmentCancelBO")
@Accessors(chain = true)
public class ShiftAdjustmentCancelBO {

    @Schema(description = "班表調整申請ID")
    @NotNull(message = "ID不能為空")
    private Long id;

    @Schema(type = "String", description = "備註")
    private String remark;
}
