DELETE FROM menu
WHERE code IN ('000316', '000316001', '000316002', '000316003', '000316004');


DELETE FROM menu 
WHERE code IN ('000317', '000317001', '000317002', '000317003');


INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
    ('000316',    '班表調整',                 '0003',   1, 1, 6),
    ('000316001', '發起班表調整申請',         '000316', 2, 1, 1),
    ('000316002', '查詢個人班表調整紀錄',     '000316', 2, 1, 2),
    ('000316003', '取消班表調整申請',         '000316', 2, 1, 3);

INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
    ('000317',    '班表審核',                 '0003',   1, 1, 7),
    ('000317001', '班表調整審核',             '000317', 2, 1, 1),
    ('000317002', '班表調整駁回',             '000317', 2, 1, 2),
    ('000317003', '查詢待審核班表調整申請',   '000317', 2, 1, 3);



UPDATE role
SET menu_permission = REPLACE(menu_permission, ',000316', '');
UPDATE role
SET menu_permission = REPLACE(menu_permission, ',000316001', '');
UPDATE role
SET menu_permission = REPLACE(menu_permission, ',000316002', '');
UPDATE role
SET menu_permission = REPLACE(menu_permission, ',000316003', '');
UPDATE role
SET menu_permission = REPLACE(menu_permission, ',000316004', ''); -- 清除舊的
UPDATE role
SET menu_permission = REPLACE(menu_permission, ',000317', '');
UPDATE role
SET menu_permission = REPLACE(menu_permission, ',000317001', '');
UPDATE role
SET menu_permission = REPLACE(menu_permission, ',000317002', '');
UPDATE role
SET menu_permission = REPLACE(menu_permission, ',000317003', '');


UPDATE role
SET menu_permission = CONCAT(
        menu_permission,
        ',000316,000316001,000316002,000316003',
        ',000317,000317001,000317002,000317003'
    )
WHERE role_name = 'ADMIN';