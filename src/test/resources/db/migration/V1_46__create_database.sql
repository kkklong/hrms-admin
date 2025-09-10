-- 添加新欄位 config_value5，用於存儲班別色碼
ALTER TABLE config
    ADD COLUMN config_value5 VARCHAR(64) COMMENT '值5';

-- 更新初始化資料，為每個班別設置顏色編碼
UPDATE config SET config_value5 = '#FFA500' WHERE config_key = 'SHIFT_TYPE_GENERAL_DAY';
UPDATE config SET config_value5 = '#20B2AA' WHERE config_key = 'SHIFT_TYPE_GENERAL_SPECIAL';
UPDATE config SET config_value5 = '#A52A2A' WHERE config_key = 'SHIFT_TYPE_SCHEDULED_DAY';
UPDATE config SET config_value5 = '#800080' WHERE config_key = 'SHIFT_TYPE_SCHEDULED_EARLY';
UPDATE config SET config_value5 = '#FF69B4' WHERE config_key = 'SHIFT_TYPE_SCHEDULED_AFTERNOON';
UPDATE config SET config_value5 = '#0000FF' WHERE config_key = 'SHIFT_TYPE_SCHEDULED_NIGHT';
UPDATE config SET config_value5 = '#FFD700' WHERE config_key = 'REST_HOLIDAY';
UPDATE config SET config_value5 = '#AFEEEE' WHERE config_key = 'REGULAR_HOLIDAY';
UPDATE config SET config_value5 = '#A9A9A9' WHERE config_key = 'NATIONAL_HOLIDAY';