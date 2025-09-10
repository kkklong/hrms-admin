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
@TableName("leave_special_records_template")
public class LeaveSpecialRecordsTemplate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 0:特別休假、1:選休假、2:事假、3:普通傷病假、4:有薪病假、5:生理假、6:生理病假、7:家庭照顧假
     */
    private String leaveTypes;

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
     * 計算期間類型（如：年度、從到職日開始等）0:曆年制 、1:週年制
     */
    private String calculationPeriod;

    /**
     * 是否要求連續請假（0: 否，1: 是）
     */
    private Boolean continuousLeave;

    /**
     * 是否需要提前提出請假申請（0: 否，1: 是）
     */
    private Boolean advanceApplication;

    /**
     * 特別休假與選休假對應的欄位，所以特別休假日與選休假會有多筆資訊
     */
    private Float yearData;

    /**
     * 0:false;1:true (是否需要附件)
     */
    private Boolean attachmentRequired;
}
