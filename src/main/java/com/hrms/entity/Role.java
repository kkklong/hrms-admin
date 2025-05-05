package com.hrms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
public class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 人資,一般員工,主管,技術長,助理,老闆,其他
     */
    private String roleName;

    /**
     * 可使用的菜單權限
     */
    private String menuPermission;

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
