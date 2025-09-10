-- 排班table
CREATE TABLE shift_schedules
(
    id             INT AUTO_INCREMENT PRIMARY KEY COMMENT '排班ID(自动生成)',
    employee_id    INT COMMENT '员工ID' NOT NULL,
    department_id  INT COMMENT '部门ID',
    remark         TEXT COMMENT '备注，记录额外信息，如特殊说明等',
    status         TINYINT COMMENT '排班状态，0：上班，1：请假' NOT NULL,
    shift_date     DATE COMMENT '排班的具体日期，如2024-08-29' NOT NULL,
    shift_types    VARCHAR(50) COMMENT '班别' NOT NULL,
    week_type      TINYINT COMMENT '用于表示该日属于第1周还是第2周。1 表示第1周，2 表示第2周' NOT NULL,
    created_date   DATETIME COMMENT '记录创建时间' NOT NULL,
    updated_date   DATETIME COMMENT '记录更新时间',
    created_id     VARCHAR(10) COMMENT '记录创建者(employee.account)',
    updated_id     VARCHAR(10) COMMENT '记录更新者(employee.account)',
    action_type    TINYINT DEFAULT 0 COMMENT '班别修改状态 0:可修改、1:不可修改 (默认:0)'
) 