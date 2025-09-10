
update menu set text = '儲存個人請假紀錄' where code = '000304001';

truncate table file_data;
truncate table leave_records;
truncate table leave_records_date_time;
truncate table leave_special_records;
truncate table notice;
truncate table submit_notification;

truncate table employee;
truncate table department;
truncate table role;

INSERT INTO employee (full_name, nick_name, position, status, salary, account, password, department_id, gender, birthday, phone, email, entry_date, created_date, updated_date, created_id, updated_id, role_id)
VALUES
 ('管理員', 'admin', '管理員', 1, 100000, 'admin', '123456', 1, '男', '2000-01-01', '0912345678', null, '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 1)
,('Manager', 'Manager', '總經理', 1, 100000, 'manager', '123456', 2, '男', '2000-01-01', '0912345678', null, '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 2)
,('Ben', 'Ben', '技術長', 1, 100000, 'ben', '123456', 3, '男', '2000-01-01', '0912345678', null, '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 2)
,('Boson', 'Boson', 'java主管', 1, 100000, 'boson', '123456', 4, '男', '2000-01-01', '0912345678', null, '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 4)
,('Rene', 'Rene', '工程師', 1, 100000, 'rene', '123456', 4, '男', '2024-06-21', '0911222333', 'J-Rene@tri-soaring.xyz', '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 5)
,('Lung', 'lung', '工程師', 1, 100000, 'lung', '123456', 4, '男', '2024-06-25', '0912345876', 'kenliner@gmail.com', '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 5)
,('billy', 'billy', '工程师', 1, 100000, 'billy', '123456', 4, '男', '2001-01-26', '0913378884', 'billy2998731@gmail.com', '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 5)
,('kurth', 'kurth', '工程师', 1, 100000, 'kurth', '123456', 4, '男', '2001-01-26', '0913378884', 'J-kurth@tri-soaring.xyz', '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 5)
,('Yvonne', 'Yvonne', '人資主管', 1, 100000, 'yvonne', '123456', 5, '女', '2001-01-26', '0913378884', null, '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 3)
,('Joyce', 'Joyce', '人資', 1, 100000, 'joyce', '123456', 5, '女', '2001-01-26', '0913378884', null, '2024-01-01', current_timestamp, current_timestamp, 'admin', 'admin', 3);

INSERT INTO department (department_name, description, manager_id, created_date, updated_date, created_id, updated_id)
VALUES
 ('管理員', '管理員', 2, current_timestamp, current_timestamp, 'admin', 'admin')
,('總經理', '總經理', 1, current_timestamp, current_timestamp, 'admin', 'admin')
,('技術長', '技術長部門', 1, current_timestamp, current_timestamp, 'admin', 'admin')
,('java', 'java部門', 2, current_timestamp, current_timestamp, 'admin', 'admin')
,('人資', '人資部門', 1, current_timestamp, current_timestamp, 'admin', 'admin');

insert into role (role_name, created_date, updated_date, created_id, updated_id) values
 ('ADMIN', current_timestamp, current_timestamp, 'admin', 'admin')
,('MANAGER', current_timestamp, current_timestamp, 'admin', 'admin')
,('HR', current_timestamp, current_timestamp, 'admin', 'admin')
,('TEAM LEADER', current_timestamp, current_timestamp, 'admin', 'admin')
,('EMPLOYEE', current_timestamp, current_timestamp, 'admin', 'admin');

update `role` set menu_permission =
(select group_concat(code order by code) from menu)
where id = 1;

# 總經理、技術長
update `role` set menu_permission =
(select group_concat(code order by code) from menu where text not in
    ('權限管理', '角色權限', '查詢角色權限列表', '查詢角色權限', '創建角色權限', '修改角色權限', '刪除角色權限', '查詢權限選單'
    , '各假別預設設定', '查詢預設假別表', '更新預設假別', '假別設定', '儲存假別設定', '刪除假別設定', '查詢員工假別設定', '新增年份假別', '查詢所有員工假別設定'
    )
)
where id = 2;

# HR
update `role` set menu_permission =
(select group_concat(code order by code) from menu where text not in
    ('權限管理', '角色權限', '查詢角色權限列表', '查詢角色權限', '創建角色權限', '修改角色權限', '刪除角色權限', '查詢權限選單')
)
where id = 3;

# TEAM LEADER
update `role` set menu_permission =
(select group_concat(code order by code) from menu where text not in
    ('權限管理', '角色權限', '查詢角色權限列表', '查詢角色權限', '創建角色權限', '修改角色權限', '刪除角色權限', '查詢權限選單'
    , '人事資料管理', '部門資料', '查詢所有部門資料', '更新部門資料', '創建部門資料', '刪除部門資料', '員工資料', '查詢所有員工資料', '查詢員工基本資料', '更新員工基本資料', '創建員工基本資料', '刪除員工基本資料', '重置員工登入密碼'
    , '公告管理', '查詢所有通知與公告', '更新公告', '創建公告', '刪除公告', '儲存並發送通知與公告'
    , '各假別預設設定', '查詢預設假別表', '更新預設假別', '假別設定', '儲存假別設定', '刪除假別設定', '查詢員工假別設定', '新增年份假別', '查詢所有員工假別設定'
    )
)
where id = 4;

# EMPLOYEE
update `role` set menu_permission =
(select group_concat(code order by code) from menu where text not in
    ('權限管理', '角色權限', '查詢角色權限列表', '查詢角色權限', '創建角色權限', '修改角色權限', '刪除角色權限', '查詢權限選單'
    , '人事資料管理', '部門資料', '查詢所有部門資料', '更新部門資料', '創建部門資料', '刪除部門資料', '員工資料', '查詢所有員工資料', '查詢員工基本資料', '更新員工基本資料', '創建員工基本資料', '刪除員工基本資料', '重置員工登入密碼'
    , '公告管理', '查詢所有通知與公告', '更新公告', '創建公告', '刪除公告', '儲存並發送通知與公告'
    , '審核請假紀錄', '查詢待審核的請假記錄', '核准請假紀錄', '駁回請假紀錄'
    , '各假別預設設定', '查詢預設假別表', '更新預設假別', '假別設定', '儲存假別設定', '刪除假別設定', '查詢員工假別設定', '新增年份假別', '查詢所有員工假別設定'
    )
)
where id = 5;
