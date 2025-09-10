INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES ('000314002', '核准加班紀錄', '000314', 1, 1, 1)
     , ('000314003', '駁回加班紀錄', '000314', 1, 1, 1)
     , ('000313003', '取消個人加班申請', '000313', 1, 1, 1);

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000313003')
WHERE role_name in ('EMPLOYEE');

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000314002,000314003,000313003')
WHERE role_name not in ('EMPLOYEE');

