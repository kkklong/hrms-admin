
update `role` set menu_permission =
    (select group_concat(code, ',') from menu)
where id = 1;

delete from menu where code = '000002003';

update menu set text = '公告管理' where code = '000201';
insert into menu(code, text, super_code, cate, showed, sort) values
    ('000202', '公告通知', '0002', 1, 1, 1)
;

drop table employee_roles;
