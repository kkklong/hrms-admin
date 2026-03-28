package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "遠端稽查規則 VO")
public class RemoteAuditConfigBo {

    @Schema(type = "Integer", description = "每日抽查總次數")
    private Integer dailyCheckTotal;

    @Schema(type = "Integer", description = "回應逾時分鐘數")
    private Integer responseTimeoutMin;

    @Schema(type = "Integer", description = "重試延遲分鐘數")
    private Integer retryDelayMin;

    @Schema(type = "Integer", description = "最大重試次數")
    private Integer retryMaxTimes;

    @Schema(type = "Integer", description = "未回應警示閾值")
    private Integer alertThresholdMiss;

    @Schema(type = "Boolean", description = "是否發送警示給主管")
    private Boolean alertToLeader;

    @Schema(type = "Boolean", description = "是否發送警示給HR")
    private Boolean alertToHr;

    @Schema(type = "Integer", description = "排除最近分鐘數")
    private Integer excludeRecentMin;
}
