
update menu set cate = 2 where cate = 1 and length(code) = 9;

update menu set code = '000307006', super_code = '000307' where  code = '000305001';

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000307006')
WHERE role_name in ('ADMIN', 'HR');

update role set menu_permission = replace(menu_permission, ',000305001', '000305001');