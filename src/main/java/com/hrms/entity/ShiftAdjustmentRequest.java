package com.hrms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 班表調整申請單 (含審核暫鎖控制)
 * </p>
 *
 * @author System
 * @since 2025-11-25
 */
@Data
@TableName(value = "shift_adjustment_request", autoResultMap = true)
public class ShiftAdjustmentRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 申請單ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申請人ID (employee.id)
     */
    private Integer applicantId;

    /**
     * 申請原因
     */
    private String reason;

    /**
     * 0=草稿,1=審核中,2=通過,3=駁回,4=取消
     */
    private Byte status;

    /**
     * 審核流程上下文/快照
     */
    private String approvalContext;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    /**
     * 核准時間
     */
    private LocalDateTime approvedAt;

    /**
     * 最終核准人
     */
    private String approvedBy;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Byte isOpen;

    /**
     * 記錄每個階段的操作過程
     */
    private String historyReview;

    /**
     * 變更明細 Map<ShiftId, NewShiftType>
     */
    private String shiftMap;
}
