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
 * @since 2025-06-12
 */
@Data
@TableName("leave_special_records")
public class LeaveSpecialRecords implements Serializable {
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
     * 如:婚假、產假、喪假....
     */
    private String leaveTypes;

    /**
     * 開始日期
     */
    private LocalDateTime startDate;

    /**
     * 結束日期
     */
    private LocalDateTime endDate;

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
     * 計薪標準，包括：0:全薪、1:半薪、2:不計薪
     */
    private String salaryStandard;

    /**
     * 是否計算全勤獎金（0: 否，1: 是）
     */
    private Boolean fullAttendanceBonus;

    /**
     * 最低請假單位
     */
    private Float minLeaveUnit;

    /**
     * 期間可請假天數上限
     */
    private Integer maxLeaveDays;

    /**
     * 是否要求連續請假（0: 否，1: 是）
     */
    private Boolean continuousLeave;

    /**
     * 是否需要提前提出請假申請（0: 否，1: 是）
     */
    private Boolean advanceApplication;

    /**
     * 僅限於說明，不參與流程限制
     */
    private String description;

    /**
     * 特休結算現金年月
     */
    private String settlementDate;

    /**
     * 特休結算現金時數
     */
    private Float settlementCount;

    /**
     * 0:false;1:true (是否需要附件)
     */
    private Boolean attachmentRequired;
}
