package com.hrms.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RemoteAuditShift implements Serializable {

    private Integer employeeId;
    private LocalDate shiftDate;
    private String shiftType;
    @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm")
    private LocalDateTime startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm")
    private LocalDateTime endTime;
}
