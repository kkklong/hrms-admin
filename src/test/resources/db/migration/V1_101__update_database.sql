INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
    ('000315', '審核流程', '0003', 1, 1, 1),
    ('000315001', '查詢審核流程設定', '000315', 2, 1, 1),
    ('000315002', '新增審核流程', '000315', 2, 1, 2),
    ('000315003', '更新審核流程', '000315', 2, 1, 3);

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000315', ',000315001', ',000315002', ',000315003')
WHERE role_name = 'ADMIN';

