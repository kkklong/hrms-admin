create table raw_attendance_records
(
    id        INT AUTO_INCREMENT PRIMARY KEY COMMENT '打卡記錄ID(自动生成)',
    date_time datetime     null COMMENT '日期',
    account  varchar(255) null COMMENT '員工帳號',
    raw_data  varchar(255) null COMMENT 'soyal原始数据'
);