package com.hrms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serial;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author System
 * @since 2025-04-13
 */
@Data
@TableName("shift_schedules")
public class ShiftSchedules implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 排班ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 員工ID
     */
    private Integer employeeId;

    /**
     * 部門ID
     */
    private Integer departmentId;

    /**
     * 備註，記錄額外信息，如特殊說明等
     */
    private String remark;

    /**
     * 排班狀態，0：上班及休假(例休日、國定假日)，1：請假
     */
    private Byte status;

    /**
     * 排班的具體日期，如2024-08-29
     */
    private LocalDate shiftDate;

    /**
     * 班別
     */
    private String shiftTypes;

    /**
     * 用於表示該日屬於第1週還是第2週。1 表示第1週，2 表示第2週
     */
    private Byte weekType;

    /**
     * 記錄創建時間
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;

    /**
     * 記錄更新時間
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedDate;

    /**
     * 記錄創建者(employee.account)
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdId;

    /**
     * 記錄更新者(employee.account)
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedId;

    /**
     * 班別修改狀態 0:可修改、1:不可修改 (默認:0)
     */
    private Byte actionType;
}
