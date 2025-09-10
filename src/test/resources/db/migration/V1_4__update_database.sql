-- employee表新增五个栏位
ALTER TABLE employee
    ADD COLUMN emergency_contact TEXT COMMENT '紧急联络人',
ADD COLUMN address VARCHAR(100) COMMENT '通讯地址',
ADD COLUMN remark TEXT COMMENT '备注说明',
ADD COLUMN floor VARCHAR(10) COMMENT '所在楼层',
ADD COLUMN seat_number VARCHAR(10) COMMENT '座位编号';

-- menu新增资料
INSERT INTO `hrm`.`menu` (`id`, `code`, `text`, `super_code`, `cate`, `showed`, `sort`, `remark`, `created_date`,
                          `updated_date`, `created_id`, `updated_id`)
VALUES (34, '000101001', '查询工作排班', '000101', 1, 1, 2, '', NULL, NULL, NULL, NULL),
       (35, '000101002', '更新工作排班', '000101', 1, 1, 3, '', NULL, NULL, NULL, NULL),
       (36, '000101003', '创建工作排班', '000101', 1, 1, 4, '', NULL, NULL, NULL, NULL),
       (37, '000101004', '删除工作排班', '000101', 1, 1, 5, '', NULL, NULL, NULL, NULL),
       (38, '000101005', '根据排班日期查询工作排班', '000101', 1, 1, 6, '', NULL, NULL, NULL, NULL),
       (39, '000101006', '根据员工ID查询工作排班', '000101', 1, 1, 7, '', NULL, NULL, NULL, NULL),
       (40, '000301001', '查询部门资料', '000301', 1, 1, 2, '', NULL, NULL, NULL, NULL),
       (41, '000301002', '更新部门资料', '000301', 1, 1, 3, '', NULL, NULL, NULL, NULL),
       (42, '000301003', '创建部门资料', '000301', 1, 1, 4, '', NULL, NULL, NULL, NULL),
       (43, '000301004', '删除部门资料', '000301', 1, 1, 5, '', NULL, NULL, NULL, NULL),
       (44, '000302001', '查询员工资料', '000302', 1, 1, 2, '', NULL, NULL, NULL, NULL),
       (45, '000302002', '更新员工资料', '000302', 1, 1, 3, '', NULL, NULL, NULL, NULL),
       (46, '000302003', '创建员工资料', '000302', 1, 1, 4, '', NULL, NULL, NULL, NULL),
       (47, '000302004', '删除员工资料', '000302', 1, 1, 5, '', NULL, NULL, NULL, NULL),
       (48, '000302005', '根据部门ID查询该部门员工资料', '000302', 1, 1, 6, '', NULL, NULL, NULL, NULL),
       (49, '000302006', '根据员工ID查询员工基本资料', '000302', 1, 1, 7, '', NULL, NULL, NULL, NULL),
       (50, '000401001', '查询线上KPI', '000401', 1, 1, 2, '', NULL, NULL, NULL, NULL),
       (51, '000401002', '更新线上KPI', '000401', 1, 1, 3, '', NULL, NULL, NULL, NULL),
       (52, '000401003', '创建线上KPI', '000401', 1, 1, 4, '', NULL, NULL, NULL, NULL),
       (53, '000401004', '删除线上KPI', '000401', 1, 1, 5, '', NULL, NULL, NULL, NULL),
       (54, '000401005', '根据用户查询个人KPI', '000401', 1, 1, 6, '', NULL, NULL, NULL, NULL),
       (55, '000401006', '根据部门查询KPI', '000401', 1, 1, 7, '', NULL, NULL, NULL, NULL),
       (56, '000401007', '根据KPI状态查询', '000401', 1, 1, 8, '', NULL, NULL, NULL, NULL),
       (57, '000402001', '查询考核记录', '000402', 1, 1, 2, '', NULL, NULL, NULL, NULL),
       (58, '000402002', '更新考核记录', '000402', 1, 1, 3, '', NULL, NULL, NULL, NULL),
       (59, '000402003', '创建考核记录', '000402', 1, 1, 4, '', NULL, NULL, NULL, NULL),
       (60, '000402004', '删除考核记录', '000402', 1, 1, 5, '', NULL, NULL, NULL, NULL),
       (61, '000402005', '根据主管ID查询该底下的所有考核记录', '000402', 1, 1, 6, '', NULL, NULL, NULL, NULL),
       (62, '000403001', '查询考核报表', '000403', 1, 1, 2, '', NULL, NULL, NULL, NULL),
       (63, '000403002', '更新考核报表', '000403', 1, 1, 3, '', NULL, NULL, NULL, NULL),
       (64, '000403003', '创建考核报表', '000403', 1, 1, 4, '', NULL, NULL, NULL, NULL),
       (65, '000403004', '删除考核报表', '000403', 1, 1, 5, '', NULL, NULL, NULL, NULL),
       (66, '000403005', '根据员工ID查询考核报表', '000403', 1, 1, 6, '', NULL, NULL, NULL, NULL),
       (67, '000403006', '根据部门ID查询考核报表', '000403', 1, 1, 7, '', NULL, NULL, NULL, NULL),
       (68, '000501001', '查询组织图', '000501', 1, 1, 2, '', NULL, NULL, NULL, NULL),
       (69, '000501002', '更新组织图', '000501', 1, 1, 3, '', NULL, NULL, NULL, NULL),
       (70, '000501003', '创建组织图', '000501', 1, 1, 4, '', NULL, NULL, NULL, NULL),
       (71, '000501004', '删除组织图', '000501', 1, 1, 5, '', NULL, NULL, NULL, NULL),
       (72, '000502001', '查询座位', '000502', 1, 1, 2, '', NULL, NULL, NULL, NULL),
       (73, '000502002', '更新座位', '000502', 1, 1, 3, '', NULL, NULL, NULL, NULL),
       (74, '000502003', '创建座位', '000502', 1, 1, 4, '', NULL, NULL, NULL, NULL),
       (75, '000502004', '删除座位', '000502', 1, 1, 5, '', NULL, NULL, NULL, NULL),
       (76, '000503001', '查询通讯录', '000503', 1, 1, 2, '', NULL, NULL, NULL, NULL),
       (77, '000503002', '更新通讯录', '000503', 1, 1, 3, '', NULL, NULL, NULL, NULL),
       (78, '000503003', '创建通讯录', '000503', 1, 1, 4, '', NULL, NULL, NULL, NULL),
       (79, '000503004', '删除通讯录', '000503', 1, 1, 5, '', NULL, NULL, NULL, NULL),
       (80, '000503005', '根据部门查询该部门底下的通讯录', '000503', 1, 1, 6, '', NULL, NULL, NULL, NULL),
       (81, '000504001', '查询通知与公告', '000504', 1, 1, 2, '', NULL, NULL, NULL, NULL),
       (82, '000504002', '更新通知与公告', '000504', 1, 1, 3, '', NULL, NULL, NULL, NULL),
       (83, '000504003', '创建通知与公告', '000504', 1, 1, 4, '', NULL, NULL, NULL, NULL),
       (84, '000504004', '删除通知与公告', '000504', 1, 1, 5, '', NULL, NULL, NULL, NULL);

-- 新增公告通知table
create table submit_notification
(
    id           int auto_increment comment '菜单ID(自动生成)' primary key,
    employee_id  int comment '員工id' not null,
    skype        varchar(50) comment 'skype帳號',
    title        varchar(50) comment '标题' not null,
    description  text comment '详细描述',
    status       tinyint comment 'true/false' not null,
    read_status  tinyint comment 'true/false 是否已读' not null,
    created_date datetime comment '创建时间',
    updated_date datetime comment '更新时间'
);

-- 新增公告模板table
create table notification_template
(
    id           int auto_increment comment '菜单ID(自动生成)' primary key,
    title        varchar(50) comment '模版名稱' not null,
    description  text comment '详细描述',
    created_date datetime comment '创建时间',
    updated_date datetime comment '更新时间',
    created_id   varchar(10) comment 'employee.account',
    updated_id   varchar(10) comment 'employee.account'
);
