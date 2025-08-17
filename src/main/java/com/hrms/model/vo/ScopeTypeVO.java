package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Schema(description = "審核類型VO")
@Accessors(chain = true)
public class ScopeTypeVO implements Serializable {

    @Schema(type = "String", description = "審核類型")
    private String key;

    @Schema(type = "String", description = "審核參數值")
    private String value;
}
