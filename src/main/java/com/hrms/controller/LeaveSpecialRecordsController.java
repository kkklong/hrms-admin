package com.hrms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hrms.common.ApiResponse;
import com.hrms.entity.Employee;
import com.hrms.enums.EmployeeStatus;
import com.hrms.model.UserInfo;
import com.hrms.model.bo.LeaveSpecialRecordsBO;
import com.hrms.model.vo.LeaveSpecialRecordsVO;
import com.hrms.model.vo.LeaveSpecialRecordsVO2;
import com.hrms.service.EmployeeService;
import com.hrms.service.LeaveSpecialRecordsService;
import com.hrms.service.LeaveSpecialRecordsTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/leaveSpecialRecords")
@Tag(name = "假別設定", description = "假別設定API")
public class LeaveSpecialRecordsController {

    @Resource
    private LeaveSpecialRecordsService leaveSpecialRecordsService;

    @Resource
    private LeaveSpecialRecordsTemplateService leaveSpecialRecordsTemplateService;

    @Resource
    private EmployeeService employeeService;

    @RequiresPermissions("000302001")
    @Operation(summary = "儲存假別設定", description = "儲存假別設定API")
    @PostMapping(path = "/saveLeaveSpecialRecords")
    public ApiResponse<String> saveLeaveRecord(@Valid @RequestBody LeaveSpecialRecordsBO leaveSpecialRecordsBO) {
        leaveSpecialRecordsService.saveLeaveSpecialRecords(leaveSpecialRecordsBO);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000302002")
    @Operation(summary = "刪除假別設定", description = "刪除假別設定API")
    @PostMapping(path = "/delete/{leaveSpecialRecordsId}")
    public ApiResponse<String> delete(@PathVariable Integer leaveSpecialRecordsId) {
        leaveSpecialRecordsService.deleteById(leaveSpecialRecordsId);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000302003")
    @Operation(summary = "查詢現在登入者假別設定", description = "查詢現在登入者假別設定API")
    @GetMapping(path = "/currentEmployeeLeaveSpecialRecords")
    public ApiResponse<List<LeaveSpecialRecordsVO>> currentEmployeeLeaveSpecialRecords() {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Integer employeeId = userInfo.getId();
        List<LeaveSpecialRecordsVO> leaveSpecialRecordsVOs = leaveSpecialRecordsService.queryByEmployeeId(employeeId);
        return new ApiResponse<>(leaveSpecialRecordsVOs);
    }

    @RequiresPermissions("000302004")
    @Operation(summary = "新增年份假別", description = "手動新增年份假別(yyyy)API")
    @PostMapping(path = "/saveLeaveSpecialRecordsByScheduled/{year}")
    public ApiResponse<String> updateLeaveSpecialRecordsTemplate(@PathVariable Integer year) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Employee::getStatus, EmployeeStatus.ACTIVE.getValue());
        List<Employee> employees = employeeService.list(queryWrapper);
        employees.forEach(e -> leaveSpecialRecordsTemplateService.setLeaveSpecialRecords(e, year));
        return new ApiResponse<>();
    }

    @RequiresPermissions("000302005")
    @Operation(summary = "查詢所有員工假別設定", description = "查詢所有員工假別設定API")
    @GetMapping(path = "/getAll")
    public ApiResponse<List<LeaveSpecialRecordsVO>> getAll() {
        List<LeaveSpecialRecordsVO> leaveSpecialRecordsVOs = leaveSpecialRecordsService.getAllLeaveSpecialRecords();
        return new ApiResponse<>(leaveSpecialRecordsVOs);
    }


    // ---- For UserReview ----
    @Operation(summary = "查詢現在登入者假別設定", description = "查詢現在登入者假別設定API")
    @GetMapping(path = "/currentEmployeeLeaveSpecialRecords2")
    public ApiResponse<List<LeaveSpecialRecordsVO2>> currentEmployeeLeaveSpecialRecords2() {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Integer employeeId = userInfo.getId();
        List<LeaveSpecialRecordsVO2> leaveSpecialRecordsVO2s = leaveSpecialRecordsService.queryByEmployeeId2(employeeId);
        return new ApiResponse<>(leaveSpecialRecordsVO2s);
    }

}
