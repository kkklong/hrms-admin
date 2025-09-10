
alter table employee modify column role_id int null comment '角色ID';

alter table leave_records modify column leave_types varchar(30) null comment '如:事假、病假....';
alter table leave_records modify column count_val float null comment '時間';
alter table leave_records modify column status tinyint null comment '0:送審；1：批准；2：拒绝；3:已銷假  4.銷假申請';
alter table leave_records modify column remark text null comment '備註';
alter table leave_records modify column approval_stage tinyint null comment '请假流程状态（0:審核完成 1: 组长審核中、2:人资審核中、3:技术长審核中、4:总经理審核中）';
alter table leave_records modify column attachment_required tinyint(1) null comment '是否需要附件';

alter table leave_special_records modify column leave_types varchar(30) null comment '如:婚假、產假、喪假....';

alter table leave_special_records_template modify column salary_standard varchar(10) null comment '计薪标准，包括：0:全薪、1:半薪、2:不计薪';

alter table notice modify column status tinyint null comment '0:未发布、1:已发布、2:已撤销';

alter table submit_notification modify column status tinyint null comment '状态 (true/false)';
alter table submit_notification modify column read_status tinyint null comment '是否已读 (true/false)';
alter table submit_notification modify column status text null comment '是否已读 (true/false)';
