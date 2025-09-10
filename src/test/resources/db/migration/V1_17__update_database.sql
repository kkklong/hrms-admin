START TRANSACTION;

alter table leave_records
    ADD COLUMN approval_stage_total tinyint COMMENT '請假審核階段總數(總共有幾階)';

alter table leave_records
    ADD COLUMN leave_special_records_id int COMMENT '請假紀錄對應的假別id';

alter table leave_records
    MODIFY COLUMN count_val float COMMENT '小時計算，可以有小數點';

alter table leave_special_records
    ADD COLUMN salary_standard VARCHAR(10) COMMENT '计薪标准，包括：全薪、半薪、不计薪';

alter table leave_special_records
    ADD COLUMN full_attendance_bonus tinyint COMMENT '是否计算全勤奖金（0: 否，1: 是）';

alter table leave_special_records
    ADD COLUMN min_leave_unit float COMMENT '最低请假单位';

alter table leave_special_records
    ADD COLUMN max_leave_days int COMMENT '期间可请假天数上限';

alter table leave_special_records
    ADD COLUMN continuous_leave tinyint COMMENT '是否要求连续请假（0: 否，1: 是）';

alter table leave_special_records
    ADD COLUMN advance_application tinyint COMMENT '是否需要提前提出请假申请（0: 否，1: 是）';

alter table leave_special_records
    ADD COLUMN unclaimed_handling VARCHAR(20) COMMENT '未请畢假期的处理方式（如：延遞、累計或折现）';

alter table leave_special_records
    ADD COLUMN description text COMMENT '仅限于说明，不参与流程限制';

alter table leave_special_records
    ADD COLUMN settlement_date DATETIME COMMENT '特休結算現金日期';

alter table leave_special_records
    ADD COLUMN settlement_count float COMMENT '特休結算現金時數';

alter table leave_special_records
    ADD COLUMN leave_id_parent int COMMENT '對應共同扣假id (例:有薪病假與普通傷病假天數是共用的，一年最多只能請30天普通傷病假，30天裡包含6天有薪病假)';

alter table leave_special_records
    ADD COLUMN is_attachment_required tinyint COMMENT '0:false;1:true (是否需要附件)';
COMMIT;