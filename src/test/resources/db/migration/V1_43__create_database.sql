-- 將所有 department 的 work_type 欄位做對應的預設更新
UPDATE `hrm`.`department`
SET `work_type` =
        CASE
            WHEN `department_name` IN ('技術長', 'java') THEN 'SHIFT_TYPE_SCHEDULED_DAY'
            WHEN `department_name` IN ('管理員', '總經理', '人資') THEN 'SHIFT_TYPE_GENERAL_DAY'
            END