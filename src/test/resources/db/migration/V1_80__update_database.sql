update menu SET code='000403', super_code ='004' WHERE code='000307' AND text='出勤管理';
update menu SET code='000403001', super_code ='000403'  WHERE code='000307006' AND text='打卡紀錄導入';
update menu SET code='000403002', super_code ='000403'  WHERE code='000307001' AND text='生成出勤紀錄';
update menu SET code='000403003', super_code ='000403'  WHERE code='000307002' AND text='查詢出勤紀錄';
update menu SET code='000403004', super_code ='000403'  WHERE code='000307003' AND text='修改出勤紀錄';
update menu SET code='000403005', super_code ='000403'  WHERE code='000307004' AND text='出勤紀錄導出';
update menu SET code='000403006', super_code ='000403'  WHERE code='000307005' AND text='重新結算出勤記錄';

update menu SET code='000404', super_code ='004' WHERE code='000312' and text='考勤月報表';
update menu SET code='000404001', super_code ='000404' WHERE code='000312001' AND text='新增考勤月報表';
update menu SET code='000404002', super_code ='000404' WHERE code='000312002' AND text='查詢考勤月報表';
update menu SET code='000404003', super_code ='000404' WHERE code='000312003' AND text='導出考勤月報表';

update menu SET code='000405', super_code ='004' where code='000310' and text='部門班表';
update menu SET code='000405001', super_code ='000405' where code='000309001' AND text='导出班表数据Excel';
update menu SET code='000405002', super_code ='000405' where code='000309003' AND text='載入年班表';
update menu SET code='000405003', super_code ='000405' where code='000310001' AND text='根據區間及部門查詢排班資料';