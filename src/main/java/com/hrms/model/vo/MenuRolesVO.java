package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Schema(description = "權限選單VO")
@Accessors(chain = true)
public class MenuRolesVO implements Serializable {
    @Schema(type = "String", description = "選單主頁籤code")
    private String code;
    @Schema(type = "String", description = "選單主頁籤text")
    private String text;
    @Schema(type = "Integer", description = "類型：1-功能頁面；2-頁面元素")
    private Integer cate;
}
