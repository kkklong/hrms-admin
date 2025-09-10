DELETE
FROM config
WHERE config_key = 'LEAVE_HOLIDAY';

UPDATE shift_schedules
SET status = 0
WHERE status <> 1;

