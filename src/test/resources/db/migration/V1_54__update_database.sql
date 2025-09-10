
INSERT INTO `hrm`.`menu` (`code`, `text`, `super_code`, `cate`, `showed`, `sort`)
VALUES ('000309001', '排班excel下載', '000309', 2, 1, 1);

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000309001')
WHERE role_name in ('ADMIN', 'HR');
