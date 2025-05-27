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
@TableName("submit_notification")
public class SubmitNotification implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜單ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 員工ID
     */
    private Integer employeeId;

    /**
     * skype帳號
     */
    private String skype;

    /**
     * 標題
     */
    private String title;

    /**
     * 詳細描述
     */
    private String description;

    /**
     * 0:未發布、1:已發布、2:已撤銷
     */
    private Byte status;

    /**
     * 是否已讀 (true/false)
     */
    private Byte readStatus;

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
     * 公告ID
     */
    private Integer noticeId;

    /**
     * 公告發布時間
     */
    private LocalDateTime publishDate;

    /**
     * 公告結束時間
     */
    private LocalDateTime endDate;
}
