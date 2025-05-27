package com.hrms.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "通知公告Vo")
@Accessors(chain = true)
public class SubmitNotificationVO implements Serializable {

    @Schema(type = "Integer", description = "公告ID")
    private Integer id;

    @Schema(type = "Integer", description = "通知公告ID")
    private Integer noticeId;

    @Schema(type = "Integer", description = "員工ID")
    private Integer  employeeId;

    @Schema(type = "String", description = "公告標題")
    private String title;

    @Schema(type = "String", description = "詳細描述")
    private String description;

    @Schema(type = "Byte", description = "狀態：0:未發布、1:已發布、2:已撤銷")
    private Byte status;

    @Schema(type = "Byte", description = "是否已讀")
    private Byte readStatus;

    @Schema(type = "string", format = "date-time", description = "創建時間")
    private LocalDateTime createdDate;

    @Schema(type = "string", format = "date-time", description = "公告發布時間")
    private LocalDateTime publishDate;

    @Schema(type = "string", format = "date-time", description = "公告結束時間")
    private LocalDateTime endDate;

    @Schema(description = "相關檔案列表")
    private List<FileDataVO> files;

    @Schema(type = "String", description = "公告類型，例如:一般通知、緊急通知、活動公告等")
    private String type;
}
