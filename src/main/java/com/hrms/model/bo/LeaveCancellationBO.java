package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "LeaveCancellationBO")
@Accessors(chain = true)
public class LeaveCancellationBO {

    @Schema(type = "Integer", description = "ID")
    private Integer id;

    @Schema(type = "String", description = "備註")
    private String remark;
}
