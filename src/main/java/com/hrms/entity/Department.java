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
public class Department implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部門ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 母部門
     */
    private Integer departmentParent;

    /**
     * 部門名稱
     */
    private String departmentName;

    /**
     * 描述
     */
    private String description;

    /**
     * 主管員工編號
     */
    private Integer managerId;

    /**
     * 主管別名
     */
    private String managerNickName;

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
     * 預設班別
     */
    private String workType;

    /**
     * 每日早班最少上班人數
     */
    private Integer everyDayMorningCount;

    /**
     * 每日午班最少上班人數
     */
    private Integer everyDayAfternoonCount;

    /**
     * 每日晚班最少上班人數
     */
    private Integer everyDayNightCount;

}
