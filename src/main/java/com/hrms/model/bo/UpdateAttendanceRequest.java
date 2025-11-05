package com.hrms.model.bo;

import com.hrms.model.vo.RawAttendanceRecordsVO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateAttendanceRequest implements Serializable {
    private List<RawAttendanceRecordsVO> vos;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
