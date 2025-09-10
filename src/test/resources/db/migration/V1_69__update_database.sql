-- update attendance_summary_report考勤月報表 bouns 文字說明
alter table attendance_summary_report MODIFY COLUMN bonus INT COMMENT '年節獎金';
alter table attendance_summary_report MODIFY COLUMN report_date VARCHAR(10) ;

-- update employee COLUMN
alter table employee MODIFY COLUMN labor_insurance_fee INT;
alter table employee MODIFY COLUMN health_insurance_fee INT;
alter table employee MODIFY COLUMN holiday_duty_allowance INT;
alter table employee MODIFY COLUMN afternoon_shift_allowance INT;
alter table employee MODIFY COLUMN night_shift_allowance INT;
alter table employee MODIFY COLUMN full_attendance_bonus INT;
