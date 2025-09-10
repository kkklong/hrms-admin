-- 更新 attendance_records 表的注释
ALTER TABLE attendance_records
    MODIFY COLUMN clock_in_time DATETIME COMMENT '上班',
    MODIFY COLUMN clock_out_time DATETIME COMMENT '下班',
    MODIFY COLUMN remark TEXT COMMENT '記錄額外資訊或特殊情況',
    MODIFY COLUMN status INT COMMENT '0正常，1異常，2請假，3其他',
    MODIFY COLUMN attendance_date DATE NOT NULL COMMENT '考勤日期',
    MODIFY COLUMN created_date DATETIME NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR (10) NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR (10) NULL COMMENT 'employee.account',
    MODIFY COLUMN late_minutes INT COMMENT '遲到分鐘數',
    MODIFY COLUMN early_leave_minutes INT COMMENT '早退分鐘數',
    MODIFY COLUMN absenteeism_minutes INT COMMENT '曠工分鐘數',
    MODIFY COLUMN shift_types VARCHAR (255) COMMENT '班別',
    MODIFY COLUMN his_data TEXT COMMENT '修改資訊歷史記錄';

-- 更新 clock_in_anomaly_notification 表的注释
ALTER TABLE clock_in_anomaly_notification
    MODIFY COLUMN id INT AUTO_INCREMENT COMMENT 'ID(自動生成)',
    MODIFY COLUMN employee_id INT COMMENT '員工ID' NOT NULL,
    MODIFY COLUMN anomaly_type VARCHAR (30) COMMENT '異常類型。如:(遲到, 早退, 未打卡)',
    MODIFY COLUMN description TEXT COMMENT '異常的詳細描述',
    MODIFY COLUMN clock_in_time DATETIME COMMENT '實際打卡時間',
    MODIFY COLUMN anomaly_time DATETIME COMMENT '記錄異常發生的時間',
    MODIFY COLUMN notification_status VARCHAR (30) COMMENT '通知狀態。如:(尚未通知員工,已經通知員工)',
    MODIFY COLUMN created_date DATETIME COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME COMMENT '更新時間';
-- 更新 config 表的注释
ALTER TABLE config
    MODIFY COLUMN id INT AUTO_INCREMENT COMMENT '配置ID(自動生成)',
    MODIFY COLUMN config_key VARCHAR (64) NOT NULL COMMENT '鍵',
    MODIFY COLUMN config_value VARCHAR (64) COMMENT '值',
    MODIFY COLUMN sort INT COMMENT '排序',
    MODIFY COLUMN remark TEXT COMMENT '備註',
    MODIFY COLUMN created_date DATETIME COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR (10) COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR (10) COMMENT 'employee.account',
    MODIFY COLUMN name VARCHAR (64) COMMENT '名稱',
    MODIFY COLUMN config_value1 VARCHAR (64) COMMENT '值1',
    MODIFY COLUMN config_value2 VARCHAR (64) COMMENT '值2',
    MODIFY COLUMN config_value3 VARCHAR (64) COMMENT '值3',
    MODIFY COLUMN config_value4 VARCHAR (64) COMMENT '值4',
    MODIFY COLUMN config_value5 VARCHAR (64) COMMENT '值5',
    MODIFY COLUMN config_value6 VARCHAR (64) COMMENT '值6';

-- 更新 department 表的注释
ALTER TABLE department
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT '部門ID(自動生成)',
    MODIFY COLUMN department_parent INT DEFAULT NULL COMMENT '母部門',
    MODIFY COLUMN department_name VARCHAR (30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '部門名稱',
    MODIFY COLUMN description VARCHAR (100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
    MODIFY COLUMN manager_id INT DEFAULT NULL COMMENT '主管員工編號',
    MODIFY COLUMN manager_nick_name VARCHAR (30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主管別名',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN work_type VARCHAR (25) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '預設班別',
    MODIFY COLUMN every_day_morning_count INT DEFAULT NULL COMMENT '每日早班最少上班人數',
    MODIFY COLUMN every_day_afternoon_count INT DEFAULT NULL COMMENT '每日午班最少上班人數',
    MODIFY COLUMN every_day_night_count INT DEFAULT NULL COMMENT '每日晚班最少上班人數';

-- 更新 employee 表的注释
ALTER TABLE employee
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT '職員ID(自動生成)',
    MODIFY COLUMN full_name VARCHAR (30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '全名',
    MODIFY COLUMN nick_name VARCHAR (30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '別名',
    MODIFY COLUMN position VARCHAR (10) COLLATE utf8mb4_general_ci NOT NULL COMMENT '職位',
    MODIFY COLUMN status TINYINT NOT NULL COMMENT '0:離職,1:在職,2:留職,3:其他',
    MODIFY COLUMN salary DECIMAL (10,0) NOT NULL COMMENT '薪資',
    MODIFY COLUMN account VARCHAR (10) COLLATE utf8mb4_general_ci NOT NULL COMMENT '帳號',
    MODIFY COLUMN password VARCHAR (100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密碼',
    MODIFY COLUMN department_id INT DEFAULT NULL COMMENT '部門ID',
    MODIFY COLUMN gender VARCHAR (4) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '性別',
    MODIFY COLUMN birthday DATE DEFAULT NULL COMMENT '生日',
    MODIFY COLUMN phone VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '聯絡電話',
    MODIFY COLUMN email VARCHAR (50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '信箱',
    MODIFY COLUMN skype VARCHAR (50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'skype帳號',
    MODIFY COLUMN telegram VARCHAR (50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'TELEGRAM帳號',
    MODIFY COLUMN entry_date DATE DEFAULT NULL COMMENT '入職時間',
    MODIFY COLUMN out_date DATE DEFAULT NULL COMMENT '離職時間',
    MODIFY COLUMN password_update_time DATETIME DEFAULT NULL COMMENT '更新密碼時間',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN emergency_contact TEXT COLLATE utf8mb4_general_ci COMMENT '緊急聯絡人',
    MODIFY COLUMN address VARCHAR (100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '通訊地址',
    MODIFY COLUMN remark TEXT COLLATE utf8mb4_general_ci COMMENT '備註說明',
    MODIFY COLUMN floor VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所在樓層',
    MODIFY COLUMN seat_number VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '座位編號',
    MODIFY COLUMN role_id INT DEFAULT NULL COMMENT '角色ID',
    MODIFY COLUMN relationship VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '緊急聯絡人與員工的關係，例如父母、配偶、朋友等',
    MODIFY COLUMN emergency_contact_phone VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '緊急聯絡人電話',
    MODIFY COLUMN overtime_type INT DEFAULT '0' COMMENT '加班是否換錢，預設是0，目前只有eg的java是1',
    MODIFY COLUMN labor_insurance_fee INT DEFAULT NULL COMMENT '勞保費用',
    MODIFY COLUMN health_insurance_fee INT DEFAULT NULL COMMENT '健保費用',
    MODIFY COLUMN holiday_duty_allowance INT DEFAULT NULL COMMENT '假日值班津貼',
    MODIFY COLUMN afternoon_shift_allowance INT DEFAULT NULL COMMENT '午班津貼',
    MODIFY COLUMN night_shift_allowance INT DEFAULT NULL COMMENT '晚班津貼',
    MODIFY COLUMN full_attendance_bonus INT DEFAULT NULL COMMENT '全勤獎金',
    MODIFY COLUMN id_number VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '身分證字號',
    MODIFY COLUMN meal_allowance INT DEFAULT NULL COMMENT '伙食津貼',
    MODIFY COLUMN employee_number VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '員工編號',
    MODIFY COLUMN highest_education_level VARCHAR (20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '最高學歷',
    MODIFY COLUMN emergency_contact_address VARCHAR (100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '緊急聯絡人通訊地址',
    MODIFY COLUMN registered_address VARCHAR (100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '戶籍地址',
    MODIFY COLUMN voluntary_pension_contribution FLOAT DEFAULT NULL COMMENT '勞退自提。0%；1%~6%',
    MODIFY COLUMN insured_dependents_count INT DEFAULT NULL COMMENT '投保眷口數',
    MODIFY COLUMN withholding_tax INT DEFAULT NULL COMMENT '代扣稅款',
    MODIFY COLUMN company_labor_insurance_fee INT DEFAULT NULL COMMENT '公司負擔勞保費用',
    MODIFY COLUMN company_health_insurance_fee INT DEFAULT NULL COMMENT '公司負擔健保費用';

-- 更新 file_data 表的注释
ALTER TABLE file_data
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT 'ID(自動生成)',
    MODIFY COLUMN file_name VARCHAR (30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '檔名',
    MODIFY COLUMN file_url VARCHAR (300) COLLATE utf8mb4_general_ci NOT NULL COMMENT '存server相對路徑',
    MODIFY COLUMN table_name VARCHAR (30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '哪個功能',
    MODIFY COLUMN case_id INT NOT NULL COMMENT '對應的功能ID',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR (10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account';

-- 更新 leave_records 表的注释
ALTER TABLE leave_records
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT 'ID(自動生成)',
    MODIFY COLUMN employee_id INT NOT NULL COMMENT '員工ID',
    MODIFY COLUMN leave_types VARCHAR(30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '如:事假、病假....',
    MODIFY COLUMN count_val FLOAT DEFAULT NULL COMMENT '時間',
    MODIFY COLUMN reason TEXT COLLATE utf8mb4_general_ci COMMENT '請假原因',
    MODIFY COLUMN status TINYINT DEFAULT NULL COMMENT '0:送審；1：批准；2：拒絕；3:已銷假  4.銷假申請',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN remark TEXT COLLATE utf8mb4_general_ci COMMENT '備註',
    MODIFY COLUMN approval_stage TINYINT DEFAULT NULL COMMENT '請假流程狀態（0:審核完成 1:組長審核中、2:人資審核中、3:技術長審核中、4:總經理審核中）',
    MODIFY COLUMN history_review TEXT COLLATE utf8mb4_general_ci COMMENT '記錄每階段審核人與時間',
    MODIFY COLUMN attachment_required TINYINT(1) DEFAULT NULL COMMENT '是否需要附件',
    MODIFY COLUMN approval_stage_total TINYINT DEFAULT NULL COMMENT '請假審核階段總數(總共有幾階)',
    MODIFY COLUMN leave_special_records_id INT DEFAULT NULL COMMENT '請假紀錄對應的假別ID';

-- 更新 leave_special_records 表的注释
ALTER TABLE leave_special_records
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT 'ID(自動生成)',
    MODIFY COLUMN employee_id INT NOT NULL COMMENT '員工ID',
    MODIFY COLUMN leave_types VARCHAR(30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '如:婚假、產假、喪假....',
    MODIFY COLUMN start_date DATETIME DEFAULT NULL COMMENT '開始日期',
    MODIFY COLUMN end_date DATETIME DEFAULT NULL COMMENT '結束日期',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN salary_standard VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '計薪標準，包括：0:全薪、1:半薪、2:不計薪',
    MODIFY COLUMN full_attendance_bonus TINYINT(1) DEFAULT NULL COMMENT '是否計算全勤獎金（0: 否，1: 是）',
    MODIFY COLUMN min_leave_unit FLOAT DEFAULT NULL COMMENT '最低請假單位',
    MODIFY COLUMN max_leave_days INT DEFAULT NULL COMMENT '期間可請假天數上限',
    MODIFY COLUMN continuous_leave TINYINT(1) DEFAULT NULL COMMENT '是否要求連續請假（0: 否，1: 是）',
    MODIFY COLUMN advance_application TINYINT(1) DEFAULT NULL COMMENT '是否需要提前提出請假申請（0: 否，1: 是）',
    MODIFY COLUMN description TEXT COLLATE utf8mb4_general_ci COMMENT '僅限於說明，不參與流程限制',
    MODIFY COLUMN settlement_date DATETIME DEFAULT NULL COMMENT '特休結算現金日期',
    MODIFY COLUMN settlement_count FLOAT DEFAULT NULL COMMENT '特休結算現金時數',
    MODIFY COLUMN attachment_required TINYINT(1) DEFAULT NULL COMMENT '0:false;1:true (是否需要附件)';

-- 更新 leave_special_records_template 表的注释
ALTER TABLE leave_special_records_template
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT 'ID(自動生成)',
    MODIFY COLUMN leave_types VARCHAR(30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '0:特別休假、1:選休假、2:事假、3:普通傷病假、4:有薪病假、5:生理假、6:生理病假、7:家庭照顧假',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN salary_standard VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '計薪標準，包括：0:全薪、1:半薪、2:不計薪',
    MODIFY COLUMN full_attendance_bonus TINYINT(1) DEFAULT NULL COMMENT '是否計算全勤獎金（0: 否，1: 是）',
    MODIFY COLUMN min_leave_unit FLOAT DEFAULT NULL COMMENT '最低請假單位',
    MODIFY COLUMN max_leave_days INT DEFAULT NULL COMMENT '期間可請假天數上限',
    MODIFY COLUMN calculation_period VARCHAR(2) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '計算期間類型（如：年度、從到職日開始等）0:曆年制 、1:週年制',
    MODIFY COLUMN continuous_leave TINYINT(1) DEFAULT NULL COMMENT '是否要求連續請假（0: 否，1: 是）',
    MODIFY COLUMN advance_application TINYINT(1) DEFAULT NULL COMMENT '是否需要提前提出請假申請（0: 否，1: 是）',
    MODIFY COLUMN year_data FLOAT DEFAULT NULL COMMENT '特別休假與選休假對應的欄位，所以特別休假日與選休假會有多筆資訊',
    MODIFY COLUMN attachment_required TINYINT(1) DEFAULT NULL COMMENT '0:false;1:true (是否需要附件)';

-- 更新 menu 表的注释
ALTER TABLE menu
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT '菜單ID(自動生成)',
    MODIFY COLUMN code VARCHAR(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜單編碼',
    MODIFY COLUMN text VARCHAR(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜單名稱',
    MODIFY COLUMN super_code VARCHAR(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上級菜單編碼',
    MODIFY COLUMN cate INT DEFAULT NULL COMMENT '類型：1-功能頁面；2-頁面元素',
    MODIFY COLUMN showed INT DEFAULT NULL COMMENT '顯示狀態：1-顯示；2-不顯示',
    MODIFY COLUMN sort INT DEFAULT NULL COMMENT '同級排序，值越小排在越前面，最小值為1',
    MODIFY COLUMN remark VARCHAR(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜單備註說明',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account';

-- 更新 notification_template 表的注释
ALTER TABLE notification_template
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT '菜單ID(自動生成)',
    MODIFY COLUMN title VARCHAR(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名稱',
    MODIFY COLUMN description TEXT COLLATE utf8mb4_general_ci COMMENT '詳細描述',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account';

-- 更新 overtime_records 表的注释
ALTER TABLE overtime_records
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT 'ID(自動生成)',
    MODIFY COLUMN employee_id INT NOT NULL COMMENT '員工ID',
    MODIFY COLUMN overtime_date DATE DEFAULT NULL COMMENT '加班日期',
    MODIFY COLUMN start_time DATETIME NOT NULL COMMENT '加班開始時間',
    MODIFY COLUMN end_time DATETIME NOT NULL COMMENT '加班結束時間',
    MODIFY COLUMN count_val FLOAT DEFAULT NULL COMMENT '加班時長，以小時計算，可以有小數點',
    MODIFY COLUMN status TINYINT NOT NULL COMMENT '狀態：0:送審；1:批准；2:拒絕',
    MODIFY COLUMN reason TEXT COLLATE utf8mb4_general_ci COMMENT '加班原因，員工填寫的加班原因',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN remark TEXT COLLATE utf8mb4_general_ci COMMENT '備註，如加班申請失敗或被拒絕的理由等',
    MODIFY COLUMN approval_stage TINYINT DEFAULT NULL COMMENT '審核流程狀態：0:審核完成；1:組長審核中；2:人資審核中；3:技術長審核中；4:總經理審核中',
    MODIFY COLUMN history_review TEXT COLLATE utf8mb4_general_ci COMMENT '記錄每個階段的審核人與時間',
    MODIFY COLUMN approval_stage_total TINYINT DEFAULT NULL COMMENT '加班申請應經過的審核階段總數',
    MODIFY COLUMN conversion_type TINYINT DEFAULT NULL COMMENT '0:換錢 1:換假';

-- 更新 raw_attendance_records 表的注释
ALTER TABLE raw_attendance_records
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT '打卡記錄ID(自動生成)',
    MODIFY COLUMN date_time DATETIME DEFAULT NULL COMMENT '日期',
    MODIFY COLUMN account VARCHAR(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '員工帳號',
    MODIFY COLUMN raw_data VARCHAR(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'soyal原始數據';

-- 更新 role 表的注释
ALTER TABLE role
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT '角色ID(自動生成)',
    MODIFY COLUMN role_name VARCHAR(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '人資,一般員工,主管,技術長,助理,老闆,其他',
    MODIFY COLUMN menu_permission TEXT COLLATE utf8mb4_general_ci COMMENT '可使用的菜單權限',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account';

-- 更新 shift_schedules 表的注释
ALTER TABLE shift_schedules
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT '排班ID(自動生成)',
    MODIFY COLUMN employee_id INT NOT NULL COMMENT '員工ID',
    MODIFY COLUMN department_id INT DEFAULT NULL COMMENT '部門ID',
    MODIFY COLUMN remark TEXT COLLATE utf8mb4_general_ci COMMENT '備註，記錄額外信息，如特殊說明等',
    MODIFY COLUMN status TINYINT NOT NULL COMMENT '排班狀態，0：上班及休假(例休日、國定假日)，1：請假',
    MODIFY COLUMN shift_date DATE NOT NULL COMMENT '排班的具體日期，如2024-08-29',
    MODIFY COLUMN shift_types VARCHAR(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '班別',
    MODIFY COLUMN week_type TINYINT NOT NULL COMMENT '用於表示該日屬於第1週還是第2週。1 表示第1週，2 表示第2週',
    MODIFY COLUMN created_date DATETIME NOT NULL COMMENT '記錄創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '記錄更新時間',
    MODIFY COLUMN created_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '記錄創建者(employee.account)',
    MODIFY COLUMN updated_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '記錄更新者(employee.account)',
    MODIFY COLUMN action_type TINYINT DEFAULT '0' COMMENT '班別修改狀態 0:可修改、1:不可修改 (默認:0)';

-- 更新 submit_notification 表的注释
ALTER TABLE submit_notification
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT '菜單ID(自動生成)',
    MODIFY COLUMN employee_id INT NOT NULL COMMENT '員工ID',
    MODIFY COLUMN skype VARCHAR(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'skype帳號',
    MODIFY COLUMN title VARCHAR(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '標題',
    MODIFY COLUMN description TEXT COLLATE utf8mb4_general_ci COMMENT '詳細描述',
    MODIFY COLUMN status TEXT COLLATE utf8mb4_general_ci COMMENT '是否已讀 (true/false)',
    MODIFY COLUMN read_status TINYINT DEFAULT NULL COMMENT '是否已讀 (true/false)',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN notice_id INT NOT NULL COMMENT '公告ID',
    MODIFY COLUMN publish_date DATETIME DEFAULT NULL COMMENT '公告發布時間',
    MODIFY COLUMN end_date DATETIME DEFAULT NULL COMMENT '公告結束時間';

-- 更新 notice 表的注释
ALTER TABLE notice
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT COMMENT '菜單ID(自動生成)',
    MODIFY COLUMN title VARCHAR(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告標題',
    MODIFY COLUMN content TEXT COLLATE utf8mb4_general_ci COMMENT '內容',
    MODIFY COLUMN publish_date DATETIME DEFAULT NULL COMMENT '公告發布時間',
    MODIFY COLUMN end_date DATETIME DEFAULT NULL COMMENT '公告結束時間',
    MODIFY COLUMN status TINYINT DEFAULT NULL COMMENT '0:未發布、1:已發布、2:已撤銷',
    MODIFY COLUMN type VARCHAR(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '例如:一般通知、緊急通知、活動公告等',
    MODIFY COLUMN created_date DATETIME DEFAULT NULL COMMENT '創建時間',
    MODIFY COLUMN updated_date DATETIME DEFAULT NULL COMMENT '更新時間',
    MODIFY COLUMN created_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account',
    MODIFY COLUMN updated_id VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'employee.account';

