drop table if exists attendance_records;

CREATE TABLE attendance_records
(
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    employee_id         INT         NOT NULL,
    clock_in_time       DATETIME COMMENT '上班',
    clock_out_time      DATETIME COMMENT '下班',
    remark              TEXT COMMENT '记录额外信息或特殊情况',
    status              INT         COMMENT '0正常，1异常，2请假，3其他',
    attendance_date     DATE        NOT NULL COMMENT '考勤日期',
    created_date        datetime    null comment '创建时间',
    updated_date        datetime    null comment '更新时间',
    created_id          varchar(10) null comment 'employee.account',
    updated_id          varchar(10) null comment 'employee.account',
    late_minutes        INT COMMENT '迟到分钟数',
    early_leave_minutes INT COMMENT '早退分钟数',
    absenteeism_minutes INT COMMENT '旷工分钟数',
    shift_types         VARCHAR(255) COMMENT '班别',
    his_data            text COMMENT '修改資訊歷史記錄',
    UNIQUE (employee_id, attendance_date)
);

INSERT INTO employee (full_name, nick_name, position, status, salary, account, password, department_id, gender, birthday, phone, entry_date, created_date, updated_date, created_id, updated_id, role_id)
VALUES
    ('Kaka', 'Kaka', '工程师', 1, 100000, 'Kaka', '123456', 4, '男', '2001-01-26', '0913378884', '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 5),
    ('Albert', 'Albert', '工程师', 1, 100000, 'Albert', '123456', 4, '男', '2001-01-26', '0913378884', '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 5),
    ('Andy', 'Andy', '工程师', 1, 100000, 'Andy', '123456', 4, '男', '2001-01-26', '0913378884', '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 5),
    ('Rony', 'Rony', '工程师', 1, 100000, 'Rony', '123456', 4, '男', '2001-01-26', '0913378884', '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 5),
    ('Green', 'Green', '工程师', 1, 100000, 'Green', '123456', 4, '男', '2001-01-26', '0913378884', '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 5);

INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
    ('000305', '打卡紀錄', '0003', 1, 1, 1)
    , ('000305001', '打卡紀錄導入', '000305', 1, 1, 1)

    , ('000306', '個人出勤紀錄', '0003', 1, 1, 1)

    , ('000307', '出勤管理', '0003', 1, 1, 1)
    , ('000307001', '生成出勤紀錄', '000307', 2, 1, 1)
    , ('000307002', '查詢出勤紀錄', '000307', 2, 1, 1)
    , ('000307003', '修改出勤紀錄', '000307', 2, 1, 1)
    , ('000307004', '出勤紀錄導出', '000307', 1, 1, 1)
    , ('000307005', '重新結算', '000307', 1, 1, 1)
;

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000305,000305001,000306,000307,000307001,000307002,000307003,000307004,000307005')
WHERE role_name in ('ADMIN', 'HR');

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000305,000306')
WHERE role_name not in ('ADMIN', 'HR');
