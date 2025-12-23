-- 1. 移除舊有的唯一索引與欄位
DROP INDEX uk_one_open_req_per_row ON shift_adjustment_request;
DROP INDEX uk_one_open_req_per_counter ON shift_adjustment_request;
DROP INDEX idx_adj_target ON shift_adjustment_request;

ALTER TABLE shift_adjustment_request
    DROP COLUMN origin_schedule_id,
    DROP COLUMN target_date,
    DROP COLUMN target_shift_types,
    DROP COLUMN target_department_id,
    DROP COLUMN counterpart_schedule_id;

-- 2. 新增儲存批量變更細節的 JSON 欄位 (命名為 shift_map)
ALTER TABLE shift_adjustment_request
    ADD COLUMN shift_map JSON NOT NULL COMMENT '變更明細 Map<ShiftId, NewShiftType>';

-- 3. 建立索引
CREATE INDEX idx_sar_applicant ON shift_adjustment_request (applicant_id);