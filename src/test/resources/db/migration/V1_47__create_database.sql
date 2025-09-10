INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
    ('000308', '排班管理', '0003', 1, 1, 1)
    , ('000308001', '儲存班別配置', '000308', 2, 1, 1)
    , ('000308002', '載入年班表', '000308', 2, 1, 1)
    , ('000308003', '根據年月份及部門查詢排班資料', '000308', 2, 1, 1)
    , ('000308004', '區間查詢', '000308', 2, 1, 1)
;

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000308,000308001,000308002,000308003,000308004')
WHERE role_name in ('ADMIN', 'HR');

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000308,000308003,000308004')
WHERE role_name not in ('ADMIN', 'HR');
