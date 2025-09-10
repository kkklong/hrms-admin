-- 新增部門table欄位
ALTER TABLE `hrm`.`department`
    ADD COLUMN `work_type` VARCHAR(25) DEFAULT NULL COMMENT '預設班別',
ADD COLUMN `overtime_type` INT DEFAULT 0 COMMENT '加班是否換錢，預設是0，目前只有eg的java是1',
ADD COLUMN `every_day_morning_count` INT  COMMENT '每日早班最少上班人數',
ADD COLUMN `every_day_afternoon_count` INT  COMMENT '每日午班最少上班人數',
ADD COLUMN `every_day_night_count` INT  COMMENT '每日晚班最少上班人數';
