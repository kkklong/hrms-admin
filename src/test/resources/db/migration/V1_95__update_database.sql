ALTER TABLE attendance_summary_report
    ADD COLUMN restday_u2_multiplier FLOAT COMMENT '休息日前2小時加班時數';
ALTER TABLE attendance_summary_report
    ADD COLUMN restday_u8_multiplier FLOAT COMMENT '休息日前3~8小時加班時數';
ALTER TABLE attendance_summary_report
    ADD COLUMN restday_u12_multiplier FLOAT COMMENT '休息日前9~12小時加班時數';
ALTER TABLE attendance_summary_report
    ADD COLUMN regular_u8_multiplier FLOAT COMMENT '例假日前8小時加班時數';
ALTER TABLE attendance_summary_report
    ADD COLUMN regular_u12_multiplier FLOAT COMMENT '例假日前9~12小時加班時數';
ALTER TABLE attendance_summary_report
    ADD COLUMN holiday_u8_multiplier FLOAT COMMENT '休假日前8小時加班時數';
ALTER TABLE attendance_summary_report
    ADD COLUMN holiday_u10_multiplier FLOAT COMMENT '休假日前9~10小時加班時數';
ALTER TABLE attendance_summary_report
    ADD COLUMN holiday_u12_multiplier FLOAT COMMENT '休假日前11~12小時加班時數';

