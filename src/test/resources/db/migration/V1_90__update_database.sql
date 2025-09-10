INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
    ('000307', '遠端打卡管理', '0003', 1, 1, 1);

INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
    ('000307001', '儲存遠端打卡設定', '000307', 1, 1, 1),
    ('000307002', '查詢遠端打卡設定', '000307', 1, 1, 2);

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000307,000307001,000307002')
WHERE role_name IN ('ADMIN','HR','MANAGER');