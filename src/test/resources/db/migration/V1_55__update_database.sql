DELETE
FROM menu
WHERE code IN ('000308001', '000308002', '000308003', '000308004');

INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES ('000308001', '區間查詢', '000308', 2, 1, 1),
       ('000308002', '儲存員工排班', '000308', 2, 1, 1),
       ('000309001', '儲存班別配置', '000309', 2, 1, 1),
       ('000309002', '載入年班表', '000309', 2, 1, 1),
       ('000310001', '根據區間及部門查詢排班資料', '000310', 2, 1, 1);

UPDATE role
SET menu_permission = REPLACE(menu_permission, ',000308001', ''),
    menu_permission = REPLACE(menu_permission, ',000308002', ''),
    menu_permission = REPLACE(menu_permission, ',000308003', ''),
    menu_permission = REPLACE(menu_permission, ',000308004', '');

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000308001,000308002,000309001,000309002,000310001')
WHERE role_name IN ('ADMIN', 'HR');

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000308001,000308002,000310001')
WHERE role_name NOT IN ('ADMIN', 'HR');