alter table department drop foreign key fk_manager;
alter table employee drop foreign key fk_department;
alter table employee_roles drop foreign key employee_roles_ibfk_1;
alter table employee_roles drop foreign key employee_roles_ibfk_2;
alter table leave_normal_records drop foreign key leave_normal_records_ibfk_1;
alter table leave_records drop foreign key leave_records_ibfk_1;
alter table leave_special_records drop foreign key leave_special_records_ibfk_1;
alter table submit_notification drop foreign key fk_employee;
alter table submit_notification drop foreign key fk_notice;


insert into department(department_parent, department_name, description, manager_id, manager_nick_name, created_date, updated_date, created_id, updated_id) values
    (null, 'Java', '', 2, 'Boson', now(), now(), null, null);
INSERT INTO employee (full_name, nick_name, position, status, seniority, paid_vacation, last_year_paid_vacation, salary, account, password, department_id, gender, birthday, phone, email, skype, telegram, entry_date, out_date, password_update_time, created_date, updated_date, created_id, updated_id) VALUES
    ('Boson', 'Boson', '主管', 1, 5, 12, 6, 200000, 'boson', '123456', 1, '男', null, null, null, null, null, '2024-06-18', null, '2024-06-18 17:15:38', '2024-06-18 17:15:38', '2024-06-18 17:15:38', null, null);

