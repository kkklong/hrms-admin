CREATE TABLE config
(
    id            INT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID(自动生成)',
    config_key    VARCHAR(64) NOT NULL COMMENT '键',
    config_value  VARCHAR(64) COMMENT '值',
    sort          INT COMMENT '排序',
    remark        text COMMENT '備註',
    created_date  DATETIME COMMENT '創建時間',
    updated_date  DATETIME COMMENT '更新時間',
    created_id    VARCHAR(10) COMMENT 'employee.account',
    updated_id    VARCHAR(10) COMMENT 'employee.account',
    name          VARCHAR(64) COMMENT '名稱',
    config_value1 VARCHAR(64) COMMENT '值1',
    config_value2 VARCHAR(64) COMMENT '值2',
    config_value3 VARCHAR(64) COMMENT '值3',
    config_value4 VARCHAR(64) COMMENT '值4'
);
INSERT INTO config (config_key, config_value, sort, remark, created_id, updated_id, name, config_value1, config_value2,
                    config_value3, config_value4)
VALUES ('SHIFT_TYPE_GENERAL_DAY', 'true', 1,
        'config_value: 是否彈性上下班 (true)，config_value1: 上班時間，config_value2: 下班時間，config_value3: 午休開始，config_value4: 午休結束，適用於行政、TG QA、EG Java',
        null, null, '一般制日班', '09:00', '18:00', '12:30', '13:30'),
       ('SHIFT_TYPE_GENERAL_SPECIAL', 'false', 2,
        'config_value: 是否彈性上下班 (false)，config_value1: 上班時間，config_value2: 下班時間，config_value3: 午休開始，config_value4: 午休結束，適用於 EG UI',
        null, null, '一般制特殊班', '12:00', '21:00', null, null),
       ('SHIFT_TYPE_SCHEDULED_DAY', 'true', 3,
        'config_value: 是否彈性上下班 (true)，config_value1: 上班時間，config_value2: 下班時間，config_value3: 午休開始，config_value4: 午休結束，適用於 TG Java日班、前端、APP',
        null, null, '排班制日班', '09:00', '18:00', '12:30', '13:30'),
       ('SHIFT_TYPE_SCHEDULED_EARLY', 'false', 4,
        'config_value: 是否彈性上下班 (false)，config_value1: 上班時間，config_value2: 下班時間，config_value3: 午休開始，config_value4: 午休結束，適用於 TG 運維',
        null, null, '排班制早班', '08:00', '17:00', null, null),
       ('SHIFT_TYPE_SCHEDULED_AFTERNOON', 'false', 5,
        'config_value: 是否彈性上下班 (false)，config_value1: 上班時間，config_value2: 下班時間，config_value3: 午休開始，config_value4: 午休結束，適用於 TG 運維、Java、前端',
        null, null, '排班制午班', '14:00', '23:00', null, null),
       ('SHIFT_TYPE_SCHEDULED_NIGHT', 'false', 6,
        'config_value: 是否彈性上下班 (false)，config_value1: 上班時間，config_value2: 下班時間，config_value3: 午休開始，config_value4: 午休結束，適用於 TG 運維、Java',
        null, null, '排班制夜班', '23:00', '08:00', null, null);

alter table leave_records_date_time modify column shift_type varchar(64) null comment '班次類型';
