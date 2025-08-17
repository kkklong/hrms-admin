package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Schema(description = "公司名稱VO")
@Accessors(chain = true)
public class CompanyVO {

    private static final long serialVersionUID = 1L;

    @Schema(type = "String", description = "公司名稱")
    private String key;

    @Schema(type = "String", description = "公司參數值")
    private String value;
}
