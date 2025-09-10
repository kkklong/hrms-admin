
alter table leave_records change column is_attachment_required attachment_required boolean COMMENT '0:false;1:true (是否需要附件)';

alter table leave_special_records modify column full_attendance_bonus boolean COMMENT '是否计算全勤奖金（0: 否，1: 是）';
alter table leave_special_records modify column continuous_leave       boolean     null comment '是否要求连续请假（0: 否，1: 是）';
alter table leave_special_records modify column advance_application    boolean     null comment '是否需要提前提出请假申请（0: 否，1: 是）';
alter table leave_special_records change column is_attachment_required attachment_required boolean COMMENT '0:false;1:true (是否需要附件)';

alter table leave_special_records_template modify column full_attendance_bonus  boolean     null comment '是否计算全勤奖金（0: 否，1: 是）';
alter table leave_special_records_template modify column continuous_leave       boolean     null comment '是否要求连续请假（0: 否，1: 是）';
alter table leave_special_records_template modify column advance_application    boolean     null comment '是否需要提前提出请假申请（0: 否，1: 是）';
alter table leave_special_records_template change column is_attachment_required attachment_required boolean COMMENT '0:false;1:true (是否需要附件)';
