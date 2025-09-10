ALTER TABLE leave_records
drop
start_date;
ALTER TABLE leave_records
drop
end_date;

CREATE TABLE leave_records_date_time
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    start_date       DATETIME NOT NULL COMMENT '請假開始時間',
    end_date         DATETIME NOT NULL COMMENT '請假結束時間',
    count_val        FLOAT(3, 1) NOT NULL COMMENT '小時計算，可以有小數點',
    leave_records_id INT NOT NULL COMMENT '此申請對應哪個假單',
    shift_type       VARCHAR(20) NOT NULL COMMENT '班次類型，如早班、午班、大夜班'
);
