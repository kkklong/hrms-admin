ALTER TABLE leave_special_records
    drop count_val;
ALTER TABLE leave_records
    CHANGE COLUMN mark remark text;

