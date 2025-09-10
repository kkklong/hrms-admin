
-- 更新班別設置顏色編碼
UPDATE config SET config_value5 = '#F8F8FF' WHERE config_key = 'SHIFT_TYPE_GENERAL_DAY';
UPDATE config SET config_value5 = '#FFFACD' WHERE config_key = 'SHIFT_TYPE_GENERAL_SPECIAL';
UPDATE config SET config_value5 = '#F0FFFF' WHERE config_key = 'SHIFT_TYPE_SCHEDULED_DAY';
UPDATE config SET config_value5 = '#F0F8FF' WHERE config_key = 'SHIFT_TYPE_SCHEDULED_EARLY';
UPDATE config SET config_value5 = '#FAFAD2' WHERE config_key = 'SHIFT_TYPE_SCHEDULED_AFTERNOON';
UPDATE config SET config_value5 = '#F0FFF0' WHERE config_key = 'SHIFT_TYPE_SCHEDULED_NIGHT';
UPDATE config SET config_value5 = '#FA8072' WHERE config_key = 'REST_HOLIDAY';
UPDATE config SET config_value5 = '#FF69B4' WHERE config_key = 'REGULAR_HOLIDAY';
UPDATE config SET config_value5 = '#A9A9A9' WHERE config_key = 'NATIONAL_HOLIDAY';