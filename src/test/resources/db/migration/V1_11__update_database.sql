alter table submit_notification add column publish_date datetime comment '公告发布时间';
alter table submit_notification add column end_date datetime comment '公告结束时间';

INSERT INTO `hrm`.`menu` (`id`, `code`, `text`, `super_code`, `cate`, `showed`, `sort`, `remark`, `created_date`,
                          `updated_date`, `created_id`, `updated_id`)
VALUES (20, '000201005', '儲存並發送通知與公告', '000201', 2, 1, 1, '', NULL, NULL, NULL, NULL);

update role set menu_permission = replace(menu_permission, '000201004', '000201004,000201005') where menu_permission like '%000201004%';