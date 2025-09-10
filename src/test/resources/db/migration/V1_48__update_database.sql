ALTER TABLE shift_schedules ADD CONSTRAINT uk_employee_shift_date UNIQUE (employee_id, shift_date);

INSERT INTO shift_schedules (employee_id, department_id, remark, status, shift_date, shift_types, week_type, created_date, updated_date, created_id, updated_id, action_type) VALUES
(1, 1, null, 2, '2023-12-31', 'REGULAR_HOLIDAY', 2, current_timestamp, current_timestamp, 'admin', 'admin', 1);

