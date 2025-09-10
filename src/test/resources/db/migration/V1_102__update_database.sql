INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES('000315004', '刪除審核流程', '000315', 2, 1, 3);

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000315004')
WHERE role_name = 'ADMIN';

