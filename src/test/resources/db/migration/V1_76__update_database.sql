INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
('0004', '報表管理', '', 1, 1, 1)
     ,('000401', '員工名卡', '0004', 1, 1, 1)
     ,('000401001', '導出員工名卡', '000401', 2, 1, 1)
     ,('000402', '員工名冊', '0004', 1, 1, 1)
     ,('000402001', '導出員工名冊', '000402', 2, 1, 1)
;

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',0004,000401,000401001,000402,000402001')
WHERE role_name in ('ADMIN', 'HR');

