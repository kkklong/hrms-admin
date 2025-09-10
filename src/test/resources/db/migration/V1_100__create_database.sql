-- V1_100__create_database.sql
CREATE TABLE IF NOT EXISTS approval_flow_config (
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
    scope_type   VARCHAR(20)     NOT NULL COMMENT 'EMPLOYEE / DEPARTMENT / COMPANY / GLOBAL',
    scope_value  VARCHAR(64)     NOT NULL COMMENT '對應員工ID／部門ID／公司代碼或 *',
    flow_json    JSON            NOT NULL COMMENT '簽核路徑設定：{GE_24:[], LT_24:[]}',
    active       TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '1=啟用，0=停用',
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最後更新時間',
    PRIMARY KEY (id),
    UNIQUE KEY uk_scope (scope_type, scope_value)
) COMMENT='客製簽核流程設定表';

ALTER TABLE leave_records
    ADD COLUMN custom_route_json JSON NULL COMMENT '客製簽核路徑快照';

ALTER TABLE overtime_records
    ADD COLUMN custom_route_json JSON NULL COMMENT '客製簽核路徑快照';
-- 首次插入；之後若已存在同 key 就更新內容並啟用
INSERT INTO approval_flow_config (scope_type, scope_value, flow_json, active)
VALUES
    ('GLOBAL', '*',
     '{"GE_24": ["LEADER_REVIEW", "TECH_LEAD_REVIEW", "GM_REVIEW", "HR_REVIEW"], "LT_24": ["LEADER_REVIEW", "TECH_LEAD_REVIEW", "HR_REVIEW"]}',
     1)
ON DUPLICATE KEY UPDATE
                     flow_json = VALUES(flow_json),
                     active    = 1;