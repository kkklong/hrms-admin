package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Data
@Accessors(chain = true)
@Schema(description = "遠端打卡設定 VO")
public class RemoteAttendancePeriodVo {

    @Schema(type = "Integer", description = "遠端打卡設定ID")
    private Integer id;

    @Schema(type = "LocalDate", description = "遠端允許日期")
    private LocalDate remoteDate;

    @Schema(type = "List<String>", description = "允許班別列表")
    private List<String> availableShiftType;
}