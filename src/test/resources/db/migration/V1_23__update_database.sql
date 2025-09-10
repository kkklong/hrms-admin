ALTER TABLE leave_records
    MODIFY COLUMN leave_types varchar (30);
ALTER TABLE leave_special_records
    MODIFY COLUMN leave_types varchar (30);
ALTER TABLE leave_special_records_template
    MODIFY COLUMN leave_types varchar (30);
