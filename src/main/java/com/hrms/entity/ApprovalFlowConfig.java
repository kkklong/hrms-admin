package com.hrms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 客製化簽核流程設定
 * </p>
 *
 * @author System
 * @since 2025-07-31
 */
@Data
@TableName("approval_flow_config")
public class ApprovalFlowConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * PK
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * EMPLOYEE / DEPARTMENT / COMPANY / GLOBAL
     */
    private String scopeType;

    /**
     * 對應員工 ID、部門 ID、公司代碼或 *
     */
    private String scopeValue;

    /**
     * 簽核路徑設定（含 GE_24、LT_24 陣列）
     */
    private String flowJson;

    /**
     * 1=啟用，0=停用
     */
    private Integer active;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 最後更新時間
     */
    private LocalDateTime updatedAt;
}
