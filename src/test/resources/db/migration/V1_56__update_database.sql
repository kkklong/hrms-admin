UPDATE menu
SET code = '000309002'
WHERE code = '000309001' AND text = '儲存班別配置';

UPDATE menu
SET code = '000309003'
WHERE code = '000309002' AND text = '載入年班表';

UPDATE role
SET menu_permission = CONCAT(menu_permission, ',000309003')
WHERE role_name IN ('ADMIN', 'HR');