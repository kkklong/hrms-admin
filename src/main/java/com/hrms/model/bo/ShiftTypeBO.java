package com.hrms.model.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalTime;

@Data
@Schema(description = "ShiftTypeBO")
public class ShiftTypeBO {

    @Schema(type = "Integer", description = "班別配置ID")
    private Integer id;

    @NotBlank(message = "班別名稱不能為空")
    @Schema(type = "String", description = "班別名稱")
    private String shiftName;

    @NotBlank(message = "班別key不能為空")
    @Schema(type = "String", description = "班別key")
    private String shiftKey;

    @Schema(type = "String", description = "班別描述")
    private String description;

    @Schema(type = "Integer", description = "排序")
    private Integer sort;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", description = "上班時間", example = "08:00:00")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", description = "下班時間", example = "17:00:00")
    private LocalTime endTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", description = "午休開始時間", example = "12:00:00")
    private LocalTime lunchStartTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", description = "午休結束時間", example = "13:00:00")
    private LocalTime lunchEndTime;

    @Schema(type = "Boolean", description = "是否有彈性上班 (0: 否, 1: 是)")
    private Boolean flexibleWork;

    @Schema(type = "String", description = "班別色碼")
    private String shiftColorCode;

    @Schema(type = "String", description = "時段 (早/午/晚)")
    private String timeSlot;
}

