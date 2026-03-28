package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Schema(description = "稽查紀錄查詢 BO")
public class RemoteAuditRecordDataBO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(type = "Integer", description = "稽查員工Id")
    private Integer employeeId;

    @Schema(type = "string", format = "date-time", description = "稽查 開始時間")
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime startAuditTime;

    @Schema(type = "string", format = "date-time", description = "稽查 結束時間")
    private LocalDateTime endAuditTime;

    @Schema(type = "string", format = "date-time", description = "稽查截止 開始時間")
    private LocalDateTime startDeadlineTime;

    @Schema(type = "string", format = "date-time", description = "稽查截止 結束時間")
    private LocalDateTime endDeadlineTime;

    @Schema(type = "string", format = "date-time", description = "回覆 開始時間")
    private LocalDateTime startReplyTime;

    @Schema(type = "string", format = "date-time", description = "回覆 結束時間")
    private LocalDateTime endReplyTime;

    @Schema(type = "Integer", description = "狀態")
    private Integer status;

}
