DELETE
FROM `hrm`.`role`;
INSERT INTO `hrm`.`role` (`id`, `role_name`, `menu_permission`, `created_date`, `updated_date`, `created_id`,
                          `updated_id`)
VALUES (1, 'ADMIN',
        '0001,000101,000101001,000101002,000101003,000101004,000102,000102001,000102002,000102003,000102004,000102005,000102006,0002,000201,000201001,000201002,000201003,000201004,0000,00001,000001001,000001002,000001003,000001004,000001005,000002,000002001,000002002,000002003',
        NULL, NULL, NULL, NULL);
INSERT INTO `hrm`.`role` (`id`, `role_name`, `menu_permission`, `created_date`, `updated_date`, `created_id`,
                          `updated_id`)
VALUES (2, 'LEADER', '0001,000101,000101001,000102,000102001,0002,000201001,000002001', NULL, NULL, NULL,
        NULL);
INSERT INTO `hrm`.`role` (`id`, `role_name`, `menu_permission`, `created_date`, `updated_date`, `created_id`,
                          `updated_id`)
VALUES (3, 'EMPlOYEE', '0001,000101,000101001,000102,000102001,0002,000201001,000002001', NULL, NULL, NULL,
        NULL);
