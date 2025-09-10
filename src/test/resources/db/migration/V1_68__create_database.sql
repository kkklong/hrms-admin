-- add employee COLUMN
ALTER TABLE employee ADD COLUMN `id_number` VARCHAR(10) COMMENT '身分證字號';
ALTER TABLE employee ADD COLUMN `meal_allowance` INT COMMENT '伙食津貼';
ALTER TABLE employee ADD COLUMN `employee_number` VARCHAR(10) COMMENT '员工编号';
ALTER TABLE employee ADD COLUMN `highest_education_level` VARCHAR(20) COMMENT '最高學歷';
ALTER TABLE employee ADD COLUMN `emergency_contact_address` VARCHAR(100) COMMENT '緊急連絡人通訊地址';
ALTER TABLE employee ADD COLUMN `registered_address` VARCHAR(100) COMMENT '户籍地址';
ALTER TABLE employee ADD COLUMN `voluntary_pension_contribution` float COMMENT '勞退自提。0%；1%~6%';
