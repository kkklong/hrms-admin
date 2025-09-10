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
 * @since 2024-08-28
 */
@Data
@TableName("leave_records_date_time")
public class LeaveRecordsDateTime implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 請假開始時間
     */
    private LocalDateTime startDate;

    /**
     * 請假結束時間
     */
    private LocalDateTime endDate;

    /**
     * 小時計算，可以有小數點
     */
    private Float countVal;

    /**
     * 此申請對應哪個假單
     */
    private Integer leaveRecordsId;

    /**
     * 班次類型
     */
    private String shiftType;

    /**
     * 包含休息1小時(不包含:0、包含:1) ，如是1的話，請假時數要扣1小時
     */
    private Boolean includesBreak;
}
