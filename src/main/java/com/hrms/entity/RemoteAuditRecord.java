package com.hrms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("remote_audit_record")
public class RemoteAuditRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 抽查任務ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 員工ID
     */
    @TableField("employee_id")
    private Long employeeId;

    /**
     * Telegram chatId（可null，未綁定）
     */
    @TableField("chat_id")
    private String chatId;

    /**
     * 通知渠道: TELEGRAM/LINE/EMAIL
     */
    @TableField("channel")
    private String channel;

    /**
     * 稽核類型: CLOCK_IN/CLOCK_OUT/RANDOM
     */
    @TableField("audit_type")
    private String auditType;

    /**
     * 一次性token(綁定回覆)
     */
    @TableField("session_token")
    private String sessionToken;

    /**
     * Telegram message id
     */
    @TableField("message_id")
    private Long messageId;

    /**
     * 發送時間
     */
    @TableField("sent_at")
    private LocalDateTime sentAt;

    /**
     * 截止時間
     */
    @TableField("deadline_at")
    private LocalDateTime deadlineAt;

    /**
     * 回覆時間
     */
    @TableField("replied_at")
    private LocalDateTime repliedAt;

    /**
     * 員工回覆內容
     */
    @TableField("reply_text")
    private String replyText;

    /**
     * ok/no/help/button action
     */
    @TableField("reply_action")
    private String replyAction;

    /**
     * 回覆耗時(毫秒)
     */
    @TableField("latency_ms")
    private Long latencyMs;

    /**
     * SENT/RESPONDED/TIMEOUT/CANCELLED
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否已寄信
     */
    @TableField("email_notified")
    private Boolean emailNotified;

    /**
     * 通知時間
     */
    @TableField("notify_at")
    private LocalDateTime notifyAt;

    /**
     * 建立時間
     * 使用 FieldFill.INSERT，在插入時自動填充（需配置 MetaObjectHandler）
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新時間
     * 使用 FieldFill.INSERT_UPDATE，在插入和更新時自動填充（需配置 MetaObjectHandler）
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}