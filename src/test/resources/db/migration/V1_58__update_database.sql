INSERT INTO `hrm`.`menu` (`code`, `text`, `super_code`, `cate`, `showed`, `sort`)
VALUES ('000309004', '手動調整排班', '000309', 2, 1, 1);

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000309004')
WHERE role_name  in ('ADMIN', 'HR', 'TEAM LEADER', 'MANAGER');
