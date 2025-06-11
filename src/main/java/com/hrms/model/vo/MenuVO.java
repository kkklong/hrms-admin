package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "選單VO")
@Accessors(chain = true)
public class MenuVO implements Serializable {
    @Schema(type = "String", description = "選單主頁籤code")
    private String code;
    @Schema(type = "String", description = "選單主頁籤text")
    private String text;
    @Schema(type = "List<Map<String,String>>", description = "子功能code,text")
    private List<Map<String,String>> data;


}
