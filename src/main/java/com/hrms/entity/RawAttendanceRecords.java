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
 *
 * </p>
 *
 * @author System
 * @since 2024-11-22
 */
@Data
@TableName("raw_attendance_records")
public class RawAttendanceRecords implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 打卡記錄ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 日期
     */
    private LocalDateTime dateTime;

    /**
     * 員工帳號
     */
    private String account;

    /**
     * soyal原始數據
     */
    private String rawData;
}
