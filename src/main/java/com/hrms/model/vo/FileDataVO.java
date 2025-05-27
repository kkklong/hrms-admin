package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "檔案Vo")
@Accessors(chain = true)
public class FileDataVO {

    @Schema(type = "Integer", description = "檔案ID")
    private int fileId;

    @Schema(type = "String", description = "檔案名稱")
    private String fileName;

    @Schema(type = "String", description = "檔案URL")
    private String fileUrl;
}