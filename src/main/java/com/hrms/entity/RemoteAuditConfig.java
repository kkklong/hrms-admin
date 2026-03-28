package com.hrms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@TableName("remote_audit_config")
public class RemoteAuditConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 設定ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 每日抽查總次數（點名總次數）
     */
    @TableField("daily_check_total")
    private Integer dailyCheckTotal;

    /**
     * 回應時限（分鐘）
     */
    @TableField("response_timeout_min")
    private Integer responseTimeoutMin;

    /**
     * 未回應後幾分鐘再追加點名（分鐘）
     */
    @TableField("retry_delay_min")
    private Integer retryDelayMin;

    /**
     * 最多追加點名次數（不含第一次；1=共2次）
     */
    @TableField("retry_max_times")
    private Integer retryMaxTimes;

    /**
     * 當日未回應達到N次即示警
     */
    @TableField("alert_threshold_miss")
    private Integer alertThresholdMiss;

    /**
     * 未回應示警通知直屬主管
     */
    @TableField("alert_to_leader")
    private Boolean alertToLeader;

    /**
     * 未回應示警通知人資
     */
    @TableField("alert_to_hr")
    private Boolean alertToHr;

    /**
     * 額外CC Email（逗號分隔）
     */
    @TableField("alert_cc_emails")
    private String alertCcEmails;

    /**
     * 通知渠道: TELEGRAM/LINE/EMAIL
     */
    @TableField("audit_channel")
    private String auditChannel;

    /**
     * 抽樣策略: UNIFORM/WEIGHTED/ROUND_ROBIN
     */
    @TableField("random_pick_strategy")
    private String randomPickStrategy;

    /**
     * 策略參數
     */
    @TableField("random_value")
    private Integer randomValue;

    /**
     * 避免重複抽到：距離上次抽查至少N分鐘
     */
    @TableField("exclude_recent_min")
    private Integer excludeRecentMin;

    /**
     * 生效時間
     */
    @TableField(value = "effective_from", fill = FieldFill.INSERT)
    private LocalDateTime effectiveFrom;

    /**
     * 失效時間（NULL=持續有效）
     */
    @TableField("effective_to")
    private LocalDateTime effectiveTo;

    /**
     * 建立者
     */
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private String createdBy;

    /**
     * 更新者
     */
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /**
     * 建立時間
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}