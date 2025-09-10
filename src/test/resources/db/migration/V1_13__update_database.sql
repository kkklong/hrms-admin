-- 修改 leave_records 表的 start_date 列类型为datetime
ALTER TABLE leave_records
    MODIFY COLUMN start_date DATETIME;

-- 修改 leave_records 表的 end_date 列类型为datetime
ALTER TABLE leave_records
    MODIFY COLUMN end_date DATETIME;

-- 新增 leave_records 表的 mark欄位
ALTER TABLE leave_records
    ADD COLUMN mark TEXT COMMENT '备注(如请假失败，被拒绝理由可放这)';

-- 修改leave_special_records 表的 start_date 列类型为datetime
ALTER TABLE leave_special_records
    MODIFY COLUMN start_date DATETIME;

-- 修改 leave_special_records 表的 end_date 列类型为datetime
ALTER TABLE leave_special_records
    MODIFY COLUMN end_date DATETIME;

-- 刪除leave_normal_records
DROP TABLE leave_normal_records;