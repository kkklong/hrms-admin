-- add employee COLUMN
ALTER TABLE employee ADD COLUMN insured_dependents_count INT COMMENT '投保眷口数';
ALTER TABLE employee ADD COLUMN withholding_tax INT COMMENT '代扣稅款';
ALTER TABLE employee ADD COLUMN company_labor_insurance_fee INT COMMENT '公司付擔勞保费用';
ALTER TABLE employee ADD COLUMN company_health_insurance_fee INT COMMENT '公司付擔健保费用';

-- add attendance_summary_report COLUMN
ALTER TABLE attendance_summary_report ADD COLUMN insured_dependents_count INT COMMENT '投保眷口数投保眷口數';
ALTER TABLE attendance_summary_report ADD COLUMN withholding_tax INT COMMENT '代扣稅款';
ALTER TABLE attendance_summary_report ADD COLUMN company_labor_insurance_fee INT COMMENT '公司負擔勞保費用';
ALTER TABLE attendance_summary_report ADD COLUMN company_health_insurance_fee INT COMMENT '公司負擔健保費用';
