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
@TableName("file_data")
public class FileData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 檔名
     */
    private String fileName;

    /**
     * 存server相對路徑
     */
    private String fileUrl;

    /**
     * 哪個功能
     */
    private String tableName;

    /**
     * 對應的功能ID
     */
    private Integer caseId;

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
