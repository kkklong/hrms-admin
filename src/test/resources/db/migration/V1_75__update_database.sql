INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES ('000313002', '查詢個人加班紀錄', '000313', 1, 1, 1)
     , ('000314001', '查詢待審核的加班紀錄', '000314', 1, 1, 1);


UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000313002')
WHERE role_name in ('EMPLOYEE');

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000314001,000313002')
WHERE role_name not in ('EMPLOYEE');

UPDATE menu SET super_code = '0003' WHERE code = '000314';

