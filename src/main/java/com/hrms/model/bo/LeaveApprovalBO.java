package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Schema(description = "LeaveApprovalBO")
@Accessors(chain = true)
public class LeaveApprovalBO {

    @Schema(type = "Integer", description = "ID(自動生成)")
    private Integer id;

    @Schema(type = "String", description = "備註")
    private String remark;

    @Schema(type = "boolean", description = "是否需要補件")
    private Boolean attachmentRequired;
}
