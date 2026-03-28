package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(description = "建立遠端打卡設定 BO")
@AllArgsConstructor
@NoArgsConstructor
public class RemoteAttendancePeriodBO {

    @Schema(description = "遠端打卡設定項目")
    private List<RemoteAttendancePeriodDataBO> remoteAttendancePeriodDataList;
}
