UPDATE `hrm`.`role`
SET `menu_permission` = CONCAT(`menu_permission`, ',000102002')
WHERE `id` IN (2, 3);

ALTER TABLE leave_special_records
DROP COLUMN leave_id_parent,
  DROP COLUMN unclaimed_handling;