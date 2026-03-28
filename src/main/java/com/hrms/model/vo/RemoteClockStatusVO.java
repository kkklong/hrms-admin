package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RemoteClockStatusVO {

    @Schema(description = "是否可按「上班打卡」")
    private Boolean canClockIn;

    @Schema(description = "是否可按「下班打卡」")
    private Boolean canClockOut;
}