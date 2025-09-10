UPDATE `hrm`.`role`
SET `menu_permission` = CONCAT(`menu_permission`, ',000001002')
WHERE `id` IN (2, 3);