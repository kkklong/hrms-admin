package com.hrms.task;

import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.ShiftSchedulesRepository;
import com.hrms.service.ConfigService;
import com.hrms.service.LeaveSpecialRecordsTemplateService;
import com.hrms.service.NoticeService;
import com.hrms.service.SubmitNotificationService;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
public class
ScheduledTasks {
    @Resource
    private LeaveSpecialRecordsTemplateService leaveSpecialRecordsTemplateService;

    @Resource
    private EmployeeRepository employeeRepository;

//    @Resource
//    private AttendanceRecordsService attendanceRecordsService;

    @Resource
    private ShiftSchedulesRepository shiftSchedulesRepository;

    @Resource
    private ConfigService configService;

//    @Resource
//    private RawAttendanceRecordsService rawAttendanceRecordsService;

    @Resource
    private NoticeService noticeService;

    @Resource
    private SubmitNotificationService submitNotificationService;

    static DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
}
