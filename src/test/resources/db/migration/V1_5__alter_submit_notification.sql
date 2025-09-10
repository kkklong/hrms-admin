-- 修改公告通知table
ALTER TABLE submit_notification
    ADD COLUMN notice_id INT NOT NULL COMMENT '公告ID',
ADD CONSTRAINT fk_notice FOREIGN KEY (notice_id) REFERENCES notice (id),
ADD CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES employee (id);
