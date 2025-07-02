package com.hrms.controller;

import com.hrms.service.ExcelReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@Tag(name = "報表", description = "報表API")
@Slf4j
public class ReportController {

    @Resource
    ExcelReportService reportService;

    @RequiresPermissions("000405001")
    @Operation(summary = "導出班表數據Excel", description = "班表數據Excel API下載，年月份(2024-01)")
    @GetMapping(value = "/download/shiftSchedules")
    public void shiftSchedulesExcel(HttpServletResponse response
            , @RequestParam(name = "shiftDate") String shiftDate
            , @RequestParam(name = "departmentId" , required = false) Integer departmentId) {
        reportService.shiftSchedulesExcel(response, shiftDate, departmentId);
    }

//    @RequiresPermissions("000403005")
//    @Operation(summary = "出勤紀錄導出", description = "出勤紀錄導出下載API)")
//    @GetMapping(value = "/download/attendanceRecords")
//    public void attendanceRecordsExcel(HttpServletResponse response
//            , @RequestParam(name = "startDate") LocalDate startDate
//            , @RequestParam(name = "endDate") LocalDate endDate) {
//        reportService.attendanceRecordsExcel(response, startDate, endDate);
//    }
//
//    @RequiresPermissions("000404003")
//    @Operation(summary = "導出考勤月報表", description = "導出考勤月報表 API")
//    @PostMapping("/download/attendanceSummaryReport")
//    public void attendanceSummaryReportExcelDown(HttpServletResponse response, @Valid @RequestBody AttendanceSummaryReportBO attendanceSummaryReportBO){
//        reportService.attendanceSummaryReportExcel(response, attendanceSummaryReportBO);
//    }
//
//    @RequiresPermissions("000401001")
//    @Operation(summary = "導出員工名卡", description = "導出員工名卡 API")
//    @PostMapping("/download/employeeCardExcel")
//    public void employeeCardExcel(HttpServletResponse response, @Valid @RequestBody EmployeeCardExcelBO employeeCardExcelBO){
//        reportService.employeeCardExcel(response, employeeCardExcelBO);
//    }
//
//    @RequiresPermissions("000402001")
//    @Operation(summary = "導出員工名冊", description = "導出員工名冊 API")
//    @PostMapping("/download/employeeRosterExcel")
//    public void employeeRosterExcel(HttpServletResponse response, @Valid @RequestBody EmployeeRosterExcelBO employeeRosterExcelBO){
//        reportService.employeeRosterExcel(response, employeeRosterExcelBO);
//    }
//
//    @RequiresPermissions("000406001")
//    @Operation(summary = "導出員工出勤紀錄", description = "導出員工出勤紀錄 API")
//    @PostMapping(value = "/download/employeeAttendanceExcel")
//    public void employeeAttendanceExcel(HttpServletResponse response, @Valid @RequestBody EmployeeAttendanceExcelBO employeeAttendanceExcelBO) {
//        reportService.employeeAttendanceExcel(response, employeeAttendanceExcelBO);
//    }

}
