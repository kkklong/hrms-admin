ALTER TABLE hrm.shift_schedules
    MODIFY COLUMN shift_types VARCHAR(50)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci
    NOT NULL;
