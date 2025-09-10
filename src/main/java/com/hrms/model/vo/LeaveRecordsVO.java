package com.hrms.model.vo;

import com.hrms.entity.LeaveRecordsDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "LeaveRecordsVO")
@Accessors(chain = true)
public class LeaveRecordsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "Integer", description = "ID(自動生成)")
    private Integer id;

    @Schema(type = "String", description = "員工別名")
    private String nickName;

    @Schema(type = "Integer", description = "部⻔ID")
    private String departmentId;

    @Schema(type = "String", description = "請假類型，如:事假、病假等")
    private String leaveTypes;

    @Schema(type = "LocalDateTime", description = "請假申請日期")
    private LocalDateTime createdDate;

    @Schema(type = "Double", description = "總請假時長")
    private Double countVal;

    @Schema(type = "String", description = "請假原因")
    private String reason;

    @Schema(type = "String", description = "備註")
    private String remark;

    @Schema(type = "Byte", description = "0:送審；1：批准；2：拒絕；3:已銷假  4.銷假申請中")
    private Byte status;

    @Schema(type = "Byte", description = "請假流程狀態（1: 組長審核中、2:人資審核中、3:技術長審核中、4:總經理審核中）")
    private Byte approvalStage;

    @Schema(type = "String", description = "記錄每階段審核人與時間")
    private String historyReview;

    @Schema(type = "boolean", description = "是否需要附件")
    private Boolean attachmentRequired;

    @Schema(type = "Boolean", description = "是否能被當前登入者審核")
    private Boolean eligibleForApproval;

    @Schema(type = "List<LeaveRecordsDateTime>", description = "請假具體日期及班次信息")
    private List<LeaveRecordsDateTime> leaveDates;

    @Schema(description = "相關檔案列表")
    private List<FileDataVO> files;
}