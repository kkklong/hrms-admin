-- 新增或建立班表調整申請表 + 暫鎖機制
CREATE TABLE shift_adjustment_request
(
    id                      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '申請單ID',
    applicant_id            INT          NOT NULL COMMENT '申請人ID (employee.id)',
    origin_schedule_id      INT          NOT NULL COMMENT '申請人原班表ID (shift_schedules.id)',
    target_date             DATE         NOT NULL COMMENT '目標日期',
    target_shift_types      VARCHAR(50)  NOT NULL COMMENT '目標班別',
    target_department_id    INT                   DEFAULT NULL COMMENT '目標部門ID，可為NULL表示同部門',
    counterpart_schedule_id INT                   DEFAULT NULL COMMENT '互換對象的班表ID，如無互換則NULL',
    reason                  VARCHAR(255) NOT NULL COMMENT '申請原因',
    status                  TINYINT      NOT NULL DEFAULT 1 COMMENT '0=草稿,1=審核中,2=通過,3=駁回,4=取消',
    approval_context        JSON NULL COMMENT '審核流程上下文/快照',
    created_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updated_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    approved_at             DATETIME NULL COMMENT '核准時間',
    approved_by             VARCHAR(50) NULL COMMENT '最終核准人',

    -- 產生欄位，用於唯一索引，防止同一筆班表重複開單
    is_open                 TINYINT AS (CASE WHEN status = 1 THEN 1 ELSE 0 END) STORED,

    PRIMARY KEY (id),

    KEY                     idx_adj_status (status),
    KEY                     idx_adj_target (target_date, target_shift_types),
    -- 唯一索引：同一筆班表同時間只允許一張「審核中」申請
    UNIQUE KEY uk_one_open_req_per_row (origin_schedule_id, is_open),
    UNIQUE KEY uk_one_open_req_per_counter (counterpart_schedule_id, is_open)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班表調整申請單 (含審核暫鎖控制)';


-- 為現有 shift_schedules 新增暫鎖欄位
ALTER TABLE shift_schedules
    ADD COLUMN review_lock_request_id BIGINT UNSIGNED NULL COMMENT '審核暫鎖；指向申請單ID shift_adjustment_request.id';
