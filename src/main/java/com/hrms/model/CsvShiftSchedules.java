package com.hrms.model;

import com.hrms.enums.ShiftScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CsvShiftSchedules {
    private LocalDate shiftDate;
    private String remark;
    private ShiftScheduleStatus status;
}
