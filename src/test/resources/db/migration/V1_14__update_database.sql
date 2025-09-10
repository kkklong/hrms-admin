-- 修改 leave_records 表的 start_date 列类型为datetime
ALTER TABLE leave_records
    MODIFY COLUMN status tinyint COMMENT '0:送審；1：批准；2：拒绝；3:补件；4：取消';


-- 請假審核table
create table leave_approval_processes
(
    id              int auto_increment comment 'ID(自动生成)' primary key,
    process_name    varchar(10) comment '流程名称(目前分为三天含以上和以下)',
    leave_record_id int comment ' leave_records 表的 id' not null,
    approver_id     int comment ' 审核人的 employee_id' not null,
    step_order      int comment '审批步骤' not null,
    status          tinyint COMMENT '0:送審；1：批准；2：拒绝；3:补件；4：取消',
    remark          text comment '审批意见'
);



