-- 員工table
create table employee
(
    id                      int auto_increment comment '职员ID(自动生成)' primary key,
    full_name               varchar(30) comment '全名' not null,
    nick_name               varchar(30) comment '别名' not null,
    position                varchar(10) comment '职位' not null,
    status                  tinyint comment '在职 0:离职,1:在职,2:留职,3:其他' not null,
    seniority               float comment '资历' not null,
    paid_vacation           float comment '特休 以⼩时为单位' not null,
    last_year_paid_vacation float comment '去年特休 以⼩时为单位' not null,
    salary                  DECIMAL comment '薪资' not null,
    account                 varchar(10) comment '帐号' not null,
    password                varchar(100) comment '密码' not null,
    department_id           int comment '部⻔ID',
    gender                  varchar(4) comment '性别',
    birthday                date comment '生日',
    phone                   varchar(10) comment '联络电话',
    email                   varchar(50) comment '信箱',
    skype                   varchar(50) comment 'skype帳號',
    telegram                varchar(50) comment 'TELEGRAM帳號',
    entry_date              DATE comment '⼊职时间',
    out_date                DATE comment '离职时间',
    password_update_time    datetime comment '更新密码时间',
    created_date            datetime comment '创建时间',
    updated_date            datetime comment '更新时间',
    created_id              varchar(10) comment 'employee.account',
    updated_id              varchar(10) comment 'employee.account'
);

-- 部門table
create table department
(
    id                int auto_increment comment '部门ID(自动生成)' primary key,
    department_parent int comment '⺟部⻔',
    department_name   varchar(30) comment '部⻔名称' not null,
    description       varchar(100) comment '描述',
    manager_id        int comment '主管员工编号',
    manager_nick_name varchar(30) comment '主管別名',
    created_date      datetime comment '创建时间',
    updated_date      datetime comment '更新时间',
    created_id        varchar(10) comment 'employee.account',
    updated_id        varchar(10) comment 'employee.account'
);

-- 添加外键约束
ALTER TABLE department
    ADD CONSTRAINT fk_manager FOREIGN KEY (manager_id) REFERENCES employee (id) ON DELETE SET NULL;
ALTER TABLE employee
    ADD CONSTRAINT fk_department FOREIGN KEY (department_id) REFERENCES department (id) ON DELETE SET NULL;

-- 角色(權限)table
create table role
(
    id              int auto_increment comment '角色ID(自动生成)' primary key,
    role_name       varchar(30) comment '人资,一般员工,主管,技术长,助理,老板,其他' not null,
    menu_permission text comment '可使用的菜单权限',
    created_date    datetime comment '创建时间',
    updated_date    datetime comment '更新时间',
    created_id      varchar(10) comment 'employee.account',
    updated_id      varchar(10) comment 'employee.account'
);

-- 員工角色map
create table employee_roles
(
    employee_id int comment '員工ID',
    role_id     int comment '角色权限ID',
    FOREIGN KEY (employee_id) REFERENCES employee (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE
);


-- 菜單table
create table menu
(
    id           int auto_increment comment '菜单ID(自动生成)' primary key,
    code         varchar(64) comment '菜单编码' not null,
    text         varchar(64) comment '菜单名称',
    super_code   varchar(64) comment '上级菜单编码',
    cate         int comment '类型：1-功能页面；2-页面元素',
    showed       int comment '显示状态：1-显示；2-不显示',
    sort         int comment '同级排序，值越小排在越前面，最小值为1',
    remark       varchar(512) comment '菜单备注说明',
    created_date datetime comment '创建时间',
    updated_date datetime comment '更新时间',
    created_id   varchar(10) comment 'employee.account',
    updated_id   varchar(10) comment 'employee.account'
);

-- 通知table
create table notice
(
    id           int auto_increment comment '菜单ID(自动生成)' primary key,
    title        varchar(50) comment '公告标题' not null,
    content      text comment '内容',
    publish_date datetime comment '公告发布时间',
    end_date     datetime comment '公告结束时间',
    status       tinyint comment '例如：0:已发布、1:已撤销' not null,
    type         varchar(20) comment '例如:一般通知、紧急通知、活动公告等' not null,
    created_date datetime comment '创建时间',
    updated_date datetime comment '更新时间',
    created_id   varchar(10) comment 'employee.account',
    updated_id   varchar(10) comment 'employee.account'
);
-- 請假table
create table leave_records
(
    id           int auto_increment comment 'ID(自动生成)' primary key,
    employee_id  int comment '員工ID' not null,
    leave_types  int comment '如:事假、病假....' not null,
    start_date   date comment '请假开始时间',
    end_date     date comment '请假结束时间',
    count_val    int comment '時間',
    reason       text comment '請假原因',
    status       varchar(20) comment 'Pending：申请已经提交；Approved：申请已经获得批准；Rejected：申请被拒绝；Cancelled：申请被员工或管理员取消',
    created_date datetime comment '创建时间',
    updated_date datetime comment '更新时间',
    created_id   varchar(10) comment 'employee.account',
    updated_id   varchar(10) comment 'employee.account',
    FOREIGN KEY (employee_id) REFERENCES employee (id) ON DELETE CASCADE
);

-- 請假審核table(特殊假)
create table leave_special_records
(
    id           int auto_increment comment 'ID(自动生成)' primary key,
    employee_id  int comment '員工ID' not null,
    leave_types  int comment '如:婚假、產假、喪假....' not null,
    start_date   date comment '生效时间',
    end_date     date comment '失效时间',
    count_val    int comment '時數計算',
    created_date datetime comment '创建时间',
    updated_date datetime comment '更新时间',
    created_id   varchar(10) comment 'employee.account',
    updated_id   varchar(10) comment 'employee.account',
    FOREIGN KEY (employee_id) REFERENCES employee (id) ON DELETE CASCADE
);


-- 請假審核table(一般假)
create table leave_normal_records
(
    id           int auto_increment comment 'ID(自动生成)' primary key,
    employee_id  int comment '員工ID' not null,
    leave_types  int comment '如:事假、病假....' not null,
    start_date   date comment '生效时间',
    end_date     date comment '失效时间',
    count_val    int comment '時數計算',
    created_date datetime comment '创建时间',
    updated_date datetime comment '更新时间',
    created_id   varchar(10) comment 'employee.account',
    updated_id   varchar(10) comment 'employee.account',
    FOREIGN KEY (employee_id) REFERENCES employee (id) ON DELETE CASCADE
);

-- 打卡table
create table attendance_records
(
    id             int auto_increment comment 'ID(自动生成)' primary key,
    employee_id    int comment '員工ID' not null,
    clock_in_time  datetime comment '上班时间',
    clock_out_time datetime comment '下班时间'
);

-- 檔案資料table
create table file_data
(
    id           int auto_increment comment 'ID(自动生成)' primary key,
    file_name    varchar(30) comment '檔名' not null,
    file_url     varchar(300) comment '存server相對路徑' not null,
    table_name   varchar(30) comment '哪個功能' not null,
    case_id      int comment '對應的功能id' not null,
    created_date datetime comment '创建时间',
    updated_date datetime comment '更新时间',
    created_id   varchar(10) comment 'employee.account',
    updated_id   varchar(10) comment 'employee.account'
);

-- 打卡异常通知table
create table clock_in_anomaly_notification
(
    id                  int auto_increment comment 'ID(自动生成)' primary key,
    employee_id         int comment '員工ID' not null,
    anomaly_type        varchar(30) comment '异常类型。如:(迟到, 早退, 未打卡)',
    description         text comment '异常的详细描述',
    clock_in_time       datetime comment '实际打卡时间',
    anomaly_time        datetime comment '记录异常发生的时间',
    notification_status varchar(30) comment '通知状态。如:(尚未通知员工,已经通知员工)',
    created_date        datetime comment '创建时间',
    updated_date        datetime comment '更新时间'
);

-- 新增管理員帳號
INSERT INTO employee (full_name,
                      nick_name,
                      position,
                      status,
                      seniority,
                      paid_vacation,
                      salary,
                      password,
                      department_id,
                      gender,
                      birthday,
                      phone,
                      email,
                      password_update_time,
                      entry_date,
                      updated_date,
                      last_year_paid_vacation,
                      created_id,
                      created_date,
                      updated_id,
                      account)
VALUES ('管理員', -- full_name
        'admin', -- nick_name
        '管理員', -- position
        1, -- incumbency (1:在职)
        0, -- seniority
        0, -- paid_vacation
        0.00, -- salary
        '123456', -- 密碼
        NULL, -- department_id (assuming admin has no specific department)
        '男', -- gender (Not applicable)
        NULL, -- birthday
        NULL, -- phone
        NULL, -- email
        NOW(), -- password_update_time
        NOW(), -- entry_date
        NOW(), -- last_update_time
        0, -- last_year_paid_vacation
        NULL, -- creator_id
        NOW(), -- create_time
        NULL, -- last_editor_id
        'admin' -- account
       );


