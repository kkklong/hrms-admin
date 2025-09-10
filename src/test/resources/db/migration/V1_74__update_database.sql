ALTER TABLE overtime_records drop column leave_special_records_id;

ALTER TABLE overtime_records
    MODIFY COLUMN overtime_date DATE;


INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES ('000313', '個人加班紀錄', '0003', 1, 1, 1)
     , ('000314', '審核加班紀錄', '000313', 1, 1, 1)
     , ('000313001', '個人加班申请', '000313', 1, 1, 1);


UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000313,000313001')
WHERE role_name in ('EMPLOYEE');

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000313,000314,000313001')
WHERE role_name not in ('EMPLOYEE');

