
INSERT INTO menu (code, text, super_code, cate, showed, sort)
VALUES
    ('000310', '遠端打卡', '0003', 1, 1, 2);   -- sort=2，依需要自行調整


INSERT INTO menu (code, text, super_code, cate, showed, sort) VALUES
                                                                  ('000310001', '遠端上班打卡',   '000310', 1, 1, 1),
                                                                  ('000310002', '遠端下班打卡',   '000310', 1, 1, 2),
                                                                  ('000310003', '查詢遠端打卡紀錄', '000310', 1, 1, 3);
UPDATE role
SET menu_permission = CONCAT(menu_permission,
                             ',000310,000310001,000310002,000310003')
WHERE role_name IN ('ADMIN','HR','MANAGER','TEAM LEADER','EMPLOYEE');
