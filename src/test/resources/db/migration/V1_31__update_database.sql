INSERT INTO `hrm`.`menu` (`code`, `text`, `super_code`, `cate`, `showed`, `sort`, `remark`, `created_date`,
                          `updated_date`, `created_id`, `updated_id`)
VALUES
    ('000302005', '查詢所有員工假別設定', '000302', 2, 1, 1, '', NULL, NULL, NULL, NULL);

UPDATE hrm.role
SET menu_permission = REPLACE(menu_permission, '000302004,,', '000302004,,000302005,,')
WHERE id = '1';




