package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "配置BO")
public class ConfigBO {

    @Schema(type = "Integer", description = "ID(為自動生成，不用輸入)")
    private Integer id;

    @NotBlank(message = "鍵不能為空")
    @Schema(type = "String", description = "鍵")
    private String configKey;

    @Schema(type = "String", description = "值")
    private String configValue;

    @Schema(type = "Integer", description = "排序")
    private Integer sort;

    @Schema(type = "String", description = "備註，記錄額外信息")
    private String remark;

    @NotBlank(message = "名稱不能為空")
    @Schema(type = "String", description = "名稱")
    private String name;

    @Schema(type = "String", description = "值1")
    private String configValue1;

    @Schema(type = "String", description = "值2")
    private String configValue2;

    @Schema(type = "String", description = "值3")
    private String configValue3;

    @Schema(type = "String", description = "值4")
    private String configValue4;

    @Schema(type = "String", description = "值5")
    private String configValue5;

    @Schema(type = "String", description = "值6")
    private String configValue6;
}
