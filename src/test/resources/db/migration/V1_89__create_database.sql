CREATE TABLE remote_attendance_period
(
    id                   INT AUTO_INCREMENT PRIMARY KEY COMMENT '遠端打卡ID(自動產生)',
    remote_date          DATE COMMENT '遠端允許日期',
    available_shift_type VARCHAR(500) COMMENT '允許班別(複選；以「,」分開)',
    created_id           VARCHAR(10) COMMENT '記錄建立者(employee.account)',
    created_date         DATETIME COMMENT '記錄創建時間',
    updated_id           VARCHAR(10) COMMENT '記錄更新者(employee.account)',
    updated_date         DATETIME COMMENT '記錄更新時間'
)