CREATE TABLE overtime_records
(
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    employee_id              INT      NOT NULL COMMENT '員工ID',
    overtime_date            DATETIME NOT NULL COMMENT '加班日期，記錄加班發生的日期',
    start_time               DATETIME NOT NULL COMMENT '加班開始時間',
    end_time                 DATETIME NOT NULL COMMENT '加班結束時間',
    count_val                FLOAT COMMENT '加班時長，以小時計算，可以有小數點',
    status                   TINYINT  NOT NULL COMMENT '狀態：0:送審；1:批准；2:拒絕',
    reason                   TEXT COMMENT '加班原因，員工填寫的加班原因',
    created_date             datetime    null comment '创建时间',
    updated_date             datetime    null comment '更新时间',
    created_id               varchar(10) null comment 'employee.account',
    updated_id               varchar(10) null comment 'employee.account',
    remark                   TEXT COMMENT '備註，如加班申請失敗或被拒絕的理由等',
    approval_stage           TINYINT COMMENT '審核流程狀態：1:組長審核中；2:技術長審核中；3:總經理審核中；4:人資審核中',
    history_review           TEXT COMMENT '記錄每個階段的審核人與時間',
    approval_stage_total     TINYINT COMMENT '加班申請應經過的審核階段總數',
    overtime_amount          DECIMAL(10, 2) COMMENT '記錄每次加班對應的金額',
    conversion_type          TINYINT COMMENT '0:換錢 1:換假',
    leave_special_records_id INT COMMENT '此申請對應哪個假別'
);
