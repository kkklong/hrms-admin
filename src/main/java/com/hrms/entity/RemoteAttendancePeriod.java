package com.hrms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author System
 * @since 2025-04-28
 */
@Data
@TableName("remote_attendance_period")
public class RemoteAttendancePeriod implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 遠端打卡ID(自動產生)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 遠端允許日期
     */
    private LocalDate remoteDate;

    /**
     * 允許班別(複選；以「,」分開)
     */
    private String availableShiftType;

    /**
     * 記錄建立者(employee.account)
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdId;

    /**
     * 記錄創建時間
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;

    /**
     * 記錄更新者(employee.account)
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedId;

    /**
     * 記錄更新時間
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedDate;
}
