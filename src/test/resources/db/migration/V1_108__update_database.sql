-- shift_adjustment_request 新增歷史紀錄欄位
ALTER TABLE shift_adjustment_request
    ADD COLUMN history_review JSON NULL COMMENT '記錄每個階段的操作過程';
