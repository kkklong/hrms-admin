UPDATE `hrm`.`menu` SET `text` = '更新公告' WHERE `code` = '000201002';
UPDATE `hrm`.`menu` SET `text` = '創建公告' WHERE `code` = '000201003';
UPDATE `hrm`.`menu` SET `text` = '刪除公告' WHERE `code` = '000201004';
INSERT INTO `hrm`.`menu` (`code`, `text`, `super_code`, `cate`, `showed`, `sort`, `remark`, `created_date`, `updated_date`, `created_id`, `updated_id`) VALUES ('000002003', '刪除員工角色', '000002', 1, 1, 1, '', NULL, NULL, NULL, NULL);

-- 請假初始化範本
DROP TABLE IF EXISTS `leave_special_records_template`;
CREATE TABLE `leave_special_records_template`  (
   `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID(自动生成)',
   `leave_types` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
   `created_date` datetime NULL DEFAULT NULL COMMENT '创建时间',
   `updated_date` datetime NULL DEFAULT NULL COMMENT '更新时间',
   `created_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'employee.account',
   `updated_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'employee.account',
   `salary_standard` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '计薪标准，包括：全薪、半薪、不计薪',
   `full_attendance_bonus` tinyint NULL DEFAULT NULL COMMENT '是否计算全勤奖金（0: 否，1: 是）',
   `min_leave_unit` float NULL DEFAULT NULL COMMENT '最低请假单位',
   `max_leave_days` int NULL DEFAULT NULL COMMENT '期间可请假天数上限',
   `calculation_period` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '计算期间类型（如：年度、从到职日开始等）0:曆年制 、1:週年制',
   `continuous_leave` tinyint NULL DEFAULT NULL COMMENT '是否要求连续请假（0: 否，1: 是）',
   `advance_application` tinyint NULL DEFAULT NULL COMMENT '是否需要提前提出请假申请（0: 否，1: 是）',
   `unclaimed_handling` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '未请畢假期的处理方式（如：延遞、累計或折现）',
   `gender_type` int NULL DEFAULT NULL COMMENT '該假別屬於性別獨有的假 0:全部、1:男、2:女   (如生理假)',
   `leave_id_parent` int NULL DEFAULT NULL COMMENT '對應共同扣假id (例:有薪病假與普通傷病假天數是共用的，一年最多只能請30天普通傷病假，30天裡包含6天有薪病假)',
   `year_data` float NULL DEFAULT NULL COMMENT '特別休假與選休假對應的欄位，所以特別休假日與選休假會有多筆資訊',
   `is_attachment` tinyint NULL DEFAULT NULL COMMENT '0:false;1:true (是否需要附件)',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
