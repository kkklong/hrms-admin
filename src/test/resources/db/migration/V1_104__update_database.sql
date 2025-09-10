INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
    ('000403007', '查詢上班未打卡通知名單', '000403', 2, 1, 7),
    ('000403008', '更新上班未打卡通知名單', '000403', 2, 1, 8);


UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000403007', ',000403008')
WHERE role_name = 'ADMIN';
