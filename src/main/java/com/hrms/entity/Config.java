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
public class Config implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 鍵
     */
    private String configKey;

    /**
     * 值
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String configValue;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 備註
     */
    private String remark;

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
     * 名稱
     */
    private String name;

    /**
     * 值1
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String configValue1;

    /**
     * 值2
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String configValue2;

    /**
     * 值3
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String configValue3;

    /**
     * 值4
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String configValue4;

    /**
     * 值5
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String configValue5;

    /**
     * 值6
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String configValue6;
}
