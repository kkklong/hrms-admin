UPDATE `role`
SET menu_permission = CONCAT(menu_permission, ',000304003')
WHERE id BETWEEN 1 AND 5;

INSERT INTO `hrm`.`menu` (`code`, `text`, `super_code`, `cate`, `showed`, `sort`, `remark`, `created_date`,
                          `updated_date`, `created_id`, `updated_id`)
VALUES
    ('000304003', '銷假申請', '000304', 2, 1, 1, '', NULL, NULL, NULL, NULL);