package com.hrms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class Notice implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜單ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 公告標題
     */
    private String title;

    /**
     * 內容
     */
    private String content;

    /**
     * 公告發布時間
     */
    private LocalDateTime publishDate;

    /**
     * 公告結束時間
     */
    private LocalDateTime endDate;

    /**
     * 0:未發布、1:已發布、2:已撤銷
     */
    private Byte status;

    /**
     * 例如:一般通知、緊急通知、活動公告等
     */
    private String type;

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
}
