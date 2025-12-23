ALTER TABLE raw_attendance_records CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER DATABASE hrm CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;
ALTER TABLE config CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE shift_schedules CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE employee CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE department CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;