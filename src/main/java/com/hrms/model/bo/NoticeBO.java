package com.hrms.model.bo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Schema(description = "公告BO")
public class NoticeBO {

    @Schema(type = "Integer", description = "ID(為自動生成，不用輸入)")
    private Integer id;

    @NotBlank(message = "公告標題不能為空")
    @Schema(type = "String", description = "公告標題")
    private String title;

    @NotBlank(message = "內容不能為空")
    @Schema(type = "String", description = "內容")
    private String content;

    @NotNull
    @Schema(type = "string", format = "date-time", description = "公告發布時間")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime publishDate;

    @NotNull
    @Schema(type = "string", format = "date-time", description = "公告結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime endDate;

    @NotBlank(message = "公告類型不能為空")
    @Schema(type = "String", description = "公告類型，例如:一般通知、緊急通知、活動公告等")
    private String type;
}
