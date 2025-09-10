ALTER TABLE leave_records_date_time
    ADD COLUMN includes_break TINYINT(1) NULL COMMENT '包含休息1小時(不包含:0、包含:1) ，如是1的話，請假時數要扣1小時';
