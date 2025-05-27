package com.hrms.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "公告Vo")
@Accessors(chain = true)
public class NoticeVO implements Serializable {

    @Schema(type = "Integer", description = "公告ID")
    private Integer id;

    @Schema(type = "String", description = "公告標題")
    private String title;

    @Schema(type = "String", description = "內容")
    private String content;

    @Schema(type = "string", format = "date-time", description = "公告發布時間")
    private LocalDateTime publishDate;

    @Schema(type = "string", format = "date-time", description = "公告結束時間")
    private LocalDateTime endDate;

    @Schema(type = "Byte", description = "狀態：0:未發布、1:已發布、2:已撤銷")
    private Byte status;

    @Schema(type = "String", description = "公告類型，例如:一般通知、緊急通知、活動公告等")
    private String type;

    @Schema(type = "string", description = "建立者")
    private String createdId;

    @Schema(type = "LocalDateTime", description = "創建時間")
    private LocalDateTime createdDate;

    @Schema(description = "相關檔案列表")
    private List<FileDataVO> files;
}
