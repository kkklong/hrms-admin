package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Schema(description = "審核人VO")
@Accessors(chain = true)
public class LeaveRecordApprovalStageVO implements Serializable {

    @Schema(type = "String", description = "審核人員")
    private String key;

    @Schema(type = "String", description = "審核參數值")
    private String value;
}
