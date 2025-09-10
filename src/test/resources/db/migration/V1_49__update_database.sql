INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
('000309', '排班管理', '0003', 1, 1, 1)
,('000310', '部門班表', '0003', 1, 1, 1)
;

update menu set text = '個人排班' where code = '000308';

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000309,000310')
WHERE role_name in ('ADMIN', 'HR');

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000310')
WHERE role_name not in ('ADMIN', 'HR');
