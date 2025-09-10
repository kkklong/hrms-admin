package com.hrms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author System
 * @since 2024-11-22
 */
@Data
@TableName("leave_records")
public class LeaveRecords implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 員工ID
     */
    private Integer employeeId;

    /**
     * 如:事假、病假....
     */
    private String leaveTypes;

    /**
     * 時間
     */
    private Float countVal;

    /**
     * 請假原因
     */
    private String reason;

    /**
     * 0:送審；1：批准；2：拒絕；3:已銷假  4.銷假申請
     */
    private Byte status;

    /**
     * 創建時間
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;

    /**
     * 更新時間
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedDate;

    /**
     * employee.account
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdId;

    /**
     * employee.account
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedId;

    /**
     * 備註
     */
    private String remark;

    /**
     * 請假流程狀態（0:審核完成 1:組長審核中、2:人資審核中、3:技術長審核中、4:總經理審核中）
     */
    private Byte approvalStage;

    /**
     * 記錄每階段審核人與時間
     */
    private String historyReview;

    /**
     * 是否需要附件
     */
    private Boolean attachmentRequired;

    /**
     * 請假審核階段總數(總共有幾階)
     */
    private Byte approvalStageTotal;

    /**
     * 請假紀錄對應的假別ID
     */
    private Integer leaveSpecialRecordsId;
    /** 客製簽核路徑快照（JSON） */
    private String customRouteJson;
}
