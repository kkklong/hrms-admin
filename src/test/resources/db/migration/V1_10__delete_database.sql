DELETE FROM `hrm`.`employee_roles`;

ALTER TABLE employee ADD COLUMN role_id int;

UPDATE employee SET role_id = 1 WHERE account = 'admin';
UPDATE employee SET role_id = 3 WHERE account != 'admin';

DELETE FROM `hrm`.`menu` where code in ('000002','000002001','000002002','000002003');

DELETE FROM `hrm`.`role` where role_name = 'ADMIN';
INSERT INTO `hrm`.`role` (`id`, `role_name`, `menu_permission`, `created_date`, `updated_date`, `created_id`,`updated_id`)
VALUES (1, 'ADMIN','0001,000101,000101001,000101002,000101003,000101004,000102,000102001,000102002,000102003,000102004,000102005,000102006,0002,000201,000201001,000201002,000201003,000201004,0000,00001,000001001,000001002,000001003,000001004,000001005',
        NULL, NULL, NULL, NULL);

