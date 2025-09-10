CREATE TABLE attendance_alert_recipient
(
    id           INT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    employee_id  INT      NOT NULL COMMENT '收到通知的員工ID',
    event_type   TINYINT  NOT NULL COMMENT '事件別：1=上班未打卡（可擴充）',
    enabled      TINYINT  NOT NULL DEFAULT 1 COMMENT '是否啟用(1=啟用,0=停用)',
    created_date DATETIME NOT NULL COMMENT '建立時間',
    updated_date DATETIME NOT NULL COMMENT '更新時間',
    created_id   VARCHAR(64) NULL               COMMENT 'employee.account',
    updated_id   VARCHAR(64) NULL               COMMENT 'employee.account',
    UNIQUE KEY uk_emp_event (employee_id, event_type)
) COMMENT='事件通知之收件人清單';
