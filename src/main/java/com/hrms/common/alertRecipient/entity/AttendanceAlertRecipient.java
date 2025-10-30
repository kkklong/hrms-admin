package com.hrms.common.alertRecipient.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 事件通知之收件人清單
 * </p>
 *
 * @author System
 * @since 2025-08-19
 */
@Data
@TableName("attendance_alert_recipient")
public class AttendanceAlertRecipient implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 收到通知的員工ID
     */
    private Integer employeeId;

    /**
     * 事件別：1=上班未打卡（可擴充）
     */
    private Byte eventType;

    /**
     * 是否啟用(1=啟用,0=停用)
     */
    private Byte enabled;

    /**
     * 建立時間
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
}
