drop table leave_approval_processes;


alter table leave_records
    ADD COLUMN approval_stage tinyint COMMENT '请假流程状态（1: 组长審核中、2:人资審核中、3:技术长審核中、4:总经理審核中）';

alter table leave_records
    ADD COLUMN history_review text COMMENT '记录每阶段审核人与时间';

alter table leave_records
    ADD COLUMN is_attachment_required tinyint COMMENT '是否需要附件';

alter table employee
    ADD COLUMN relationship VARCHAR(10) COMMENT '紧急联络人与员工的关系，例如父母、配偶、朋友等';

alter table employee
    ADD COLUMN emergency_contact_phone VARCHAR(10) COMMENT '紧急联络人电话';




