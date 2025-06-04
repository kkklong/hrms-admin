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
public class Menu implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜單ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 菜單編碼
     */
    private String code;

    /**
     * 菜單名稱
     */
    private String text;

    /**
     * 上級菜單編碼
     */
    private String superCode;

    /**
     * 類型：1-功能頁面；2-頁面元素
     */
    private Integer cate;

    /**
     * 顯示狀態：1-顯示；2-不顯示
     */
    private Integer showed;

    /**
     * 同級排序，值越小排在越前面，最小值為1
     */
    private Integer sort;

    /**
     * 菜單備註說明
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
}
