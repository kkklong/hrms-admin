package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@Schema(description = "排班類型VO")
@Accessors(chain = true)
public class ShiftTypeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "Integer", description = "班別配置ID")
    private Integer id;

    @Schema(type = "String", description = "班別名稱")
    private String shiftName;

    @Schema(type = "String", description = "班別key")
    private String shiftKey;

    @Schema(type = "LocalTime", description = "上班時間")
    private LocalTime startTime;

    @Schema(type = "LocalTime", description = "下班時間")
    private LocalTime endTime;

    @Schema(type = "LocalTime", description = "午休開始時間")
    private LocalTime lunchStartTime;

    @Schema(type = "LocalTime", description = "午休結束時間")
    private LocalTime lunchEndTime;

    @Schema(type = "Boolean", description = "是否有彈性上班 (0: 否, 1: 是)")
    private Boolean flexibleWork;

    @Schema(type = "String", description = "班別色碼")
    private String shiftColorCode;

    @Schema(type = "String", description = "時段 (早/午/晚)")
    private String timeSlot;
}
