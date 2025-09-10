-- 添加新欄位 config_value6，用於儲存班別的時段(早、午、晚)
ALTER TABLE config
    ADD COLUMN config_value6 VARCHAR(64) COMMENT '值6';

-- 更新初始化資料，為排班班別設置對應時段
UPDATE config
SET config_value6 = 'morning'
WHERE config_key = 'SHIFT_TYPE_SCHEDULED_DAY';
UPDATE config
SET config_value6 = 'morning'
WHERE config_key = 'SHIFT_TYPE_SCHEDULED_EARLY';
UPDATE config
SET config_value6 = 'afternoon'
WHERE config_key = 'SHIFT_TYPE_SCHEDULED_AFTERNOON';
UPDATE config
SET config_value6 = 'night'
WHERE config_key = 'SHIFT_TYPE_SCHEDULED_NIGHT';

INSERT INTO `hrm`.`menu` (`code`, `text`, `super_code`, `cate`, `showed`, `sort`)
VALUES ('000309002', '排班衝突檢測', '000309', 2, 1, 1);

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000309002')
WHERE role_name in ('ADMIN', 'HR', 'TEAM LEADER', 'MANAGER');
