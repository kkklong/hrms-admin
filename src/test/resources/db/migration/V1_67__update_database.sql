DROP TABLE attendance_summary_report;

CREATE TABLE attendance_summary_report
(
    id                               INT AUTO_INCREMENT PRIMARY KEY,
    employee_id                      INT NOT NULL COMMENT '員工ID',
    report_date                      DATE NOT NULL COMMENT '報表日期(如"2024-07")',
    total_work_day                   INT COMMENT '總工作天數',
    total_present_day                INT COMMENT '實際出勤天數',
    total_late_minute                INT COMMENT '遲到總分鐘數',
    total_early_leave_minute         INT COMMENT '早退總分鐘數',
    total_absenteeism_minute         INT COMMENT '曠工總分鐘數',
    total_paid_leave_hour            FLOAT COMMENT '有薪請假的總時數',
    total_half_paid_leave_hour       FLOAT COMMENT '半薪請假的總時數',
    total_unpaid_leave_hour          FLOAT COMMENT '不計薪請假的總時數',
    total_holiday_shift_day          INT COMMENT '假日班的總天數',
    total_night_shift_day            INT COMMENT '晚班的總天數',
    total_afternoon_shift_day        INT COMMENT '午班的總天數',
    total_morning_shift_day          INT COMMENT '早班的總天數',
    total_compensatory_leave_hour    FLOAT COMMENT '加班換補休學時數',
    expired_special_leave_cash_hour  FLOAT COMMENT '到期特休換現金時數',
    remark                           TEXT COMMENT '備註',
    full_attendance_status           TINYINT COMMENT '是否發放全勤。 0:否；1:是',
    total_first_2h_multiplier        FLOAT COMMENT '前 2 小時加班時數',
    total_beyond_2h_multiplier       FLOAT COMMENT '超過 2 小時的加班時數',
    birthday_bonus                   INT COMMENT '生日禮金',
    bonus                            INT COMMENT '年節獎',
    salary                           INT COMMENT '薪資',
    overtime_converted_to_cash       INT COMMENT '加班換現金',
    holiday_duty_allowance           INT COMMENT '假日津贴',
    afternoon_shift_allowance        INT COMMENT '午班津贴',
    night_shift_allowance            INT COMMENT '晚班津贴',
    full_attendance_bonus            INT COMMENT '全勤津贴',
    bonus_total                      INT COMMENT '發放總津貼',
    labor_insurance_fee              INT COMMENT '勞保費用',
    health_insurance_fee             INT COMMENT '健保費用',
    CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES employee (id)
);


