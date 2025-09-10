
update role set menu_permission = replace(menu_permission, '000305000305001', '000305,000307006')
            where menu_permission like '%000305000305001%';


INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
    ('000311', '班別管理', '0003', 1, 1, 1);

update menu set code = '000311001', super_code = '000311' where code = '000309002';

update role set menu_permission = replace(menu_permission, ',000309002', ',000311001')
            where menu_permission like '%,000309002%';

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000311')
WHERE role_name in ('ADMIN', 'HR');
