-- 加班明細表
create table overtime_records_detail
(
    id                  int auto_increment comment 'ID(自动生成)' primary key,
    employee_id         int comment '員工ID',
    count_val           FLOAT(3, 1) NOT NULL COMMENT '小時計算，可以有小數點',
    overtime_records_id int comment '加班主表id',
    multiplier_type     varchar(10) comment '費率類型'
);
