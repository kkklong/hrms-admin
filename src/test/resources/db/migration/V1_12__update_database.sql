-- 修改 leave_records 表的 leave_types 列类型为 string
ALTER TABLE leave_records
    MODIFY COLUMN leave_types VARCHAR (10);

-- 修改 leave_special_records 表的 leave_types 列类型为 string
ALTER TABLE leave_special_records
    MODIFY COLUMN leave_types VARCHAR (10);

-- 修改 leave_normal_records 表的 leave_types 列类型为 string
ALTER TABLE leave_normal_records
    MODIFY COLUMN leave_types VARCHAR (10);