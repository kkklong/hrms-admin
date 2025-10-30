package com.hrms.common.alertRecipient.controller;

import com.hrms.common.ApiResponse;
import com.hrms.common.alertRecipient.service.AttendanceAlertRecipientService;
import com.hrms.entity.Employee;
import com.hrms.enums.AlertEventType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 事件通知之收件人清單 前端控制器
 * </p>
 *
 * @author System
 * @since 2025-08-19
 */
@Slf4j
@RestController
@RequestMapping("/attendanceAlertRecipient")
@Tag(name = "事件通知", description = "事件通知 API")
public class AttendanceAlertRecipientController {

//    @Resource
//    private AttendanceAlertRecipientService attendanceAlertRecipientService;
//
//
//    @RequiresPermissions("000403007")
//    @Operation(summary = "查詢上班未打卡 通知人員名單", description = "查詢上班未打卡 通知人員名單 API")
//    @GetMapping(path = "/getClockInCcEmployees")
//    public ApiResponse<List<Integer>> getClockInCcEmployees() {
//        List<Integer> ids = attendanceAlertRecipientService
//                .getCcEmployeesByEvent(AlertEventType.CLOCK_IN_MISS)
//                .stream()
//                .map(Employee::getId)
//                .toList();
//        return new ApiResponse<>(ids);
//    }
//
//    @RequiresPermissions("000403008")
//    @Operation(summary = "更新上班未打卡 通知人員名單", description = "更新上班未打卡 通知人員名單API")
//    @PostMapping(path = "/updateClockInCcEmployees")
//    public ApiResponse<String> updateClockInCcEmployees(@RequestBody List<Integer> employeeIds) {
//        attendanceAlertRecipientService.updateClockInCcEmployees(employeeIds);
//        return new ApiResponse<>();
//    }

}
