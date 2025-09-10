ALTER TABLE employee
    ADD COLUMN overtime_type INT DEFAULT 0 COMMENT '加班是否換錢，預設是0，目前只有eg的java是1',
    ADD COLUMN labor_insurance_fee DECIMAL(10, 2) COMMENT '勞保費用',
    ADD COLUMN health_insurance_fee DECIMAL(10, 2) COMMENT '健保費用',
    ADD COLUMN holiday_duty_allowance DECIMAL(10, 2) COMMENT '假日津貼',
    ADD COLUMN afternoon_shift_allowance DECIMAL(10, 2) COMMENT '午班津貼',
    ADD COLUMN night_shift_allowance DECIMAL(10, 2) COMMENT '晚班津貼',
    ADD COLUMN full_attendance_bonus DECIMAL(10, 2) COMMENT '全勤津貼';

alter table department drop column overtime_type;

CREATE TABLE attendance_summary_report
(
    id                            INT AUTO_INCREMENT PRIMARY KEY,
    employee_id                   INT  NOT NULL COMMENT '員工ID',
    report_date                   DATE NOT NULL COMMENT '報表日期(如"2024-07")',
    total_work_day                INT COMMENT '總工作天數',
    total_present_day             INT COMMENT '實際出勤天數',
    total_late_minute             INT COMMENT '遲到總分鐘數',
    total_early_leave_minute      INT COMMENT '早退總分鐘數',
    total_absenteeism_minute      INT COMMENT '曠工總分鐘數',
    total_paid_leave_hour         FLOAT COMMENT '有薪請假的總小時數',
    total_half_paid_leave_hour    FLOAT COMMENT '半薪請假的總小時數',
    total_unpaid_leave_hour       FLOAT COMMENT '不計薪請假的總小時數',
    total_holiday_shift_day       INT COMMENT '假日班的總天數',
    total_night_shift_day         INT COMMENT '夜班的總天數',
    total_afternoon_shift_day     INT COMMENT '午班的總天數',
    total_morning_shift_day       INT COMMENT '早班的總天數',
    total_overtime_cash_hour      FLOAT COMMENT '加班換現金時數',
    total_compensatory_leave_hour FLOAT COMMENT '加班換補休時數',
    remark                        TEXT COMMENT '備註',
    CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES employee (id)
);

INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES ('000312', '考勤月報表', '0003', 1, 1, 1),
       ('000312001', '新增考勤月報表', '000312', 2, 1, 1),
       ('000312002', '查詢考勤月報表', '000312', 2, 1, 1),
       ('000312003', '導出考勤月報表', '000312', 2, 1, 1);

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000312,000312001,000312002,000312003')
WHERE role_name IN ('ADMIN', 'HR');

