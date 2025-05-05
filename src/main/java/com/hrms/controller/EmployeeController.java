package com.hrms.controller;

import com.hrms.common.ApiResponse;
import com.hrms.model.bo.CreateEmployeeBO;
import com.hrms.model.bo.EmployeeBO;
import com.hrms.model.vo.DropDownVo;
import com.hrms.model.vo.QueryEmployeeVO;
import com.hrms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author System
 * @since 2025-04-12
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    @RequiresPermissions("000102004")
    @Operation(summary = "創建員工基本資料", description = "創建員工基本資料API")
    @PostMapping(path = "/create")
    public ApiResponse<String> create(@Valid @RequestBody CreateEmployeeBO employeeBO) {
        employeeService.createEmployee(employeeBO);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000102003")
    @Operation(summary = "更新員工基本資料", description = "更新員工基本資料API")
    @PostMapping(path = "/update")
    public ApiResponse<String> update(@Valid @RequestBody EmployeeBO employeeBO) {
        employeeService.updateEmployee(employeeBO);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000102005")
    @Operation(summary = "刪除員工基本資料", description = "刪除員工基本資料API")
    @PostMapping(path = "/delete/{employeeId}")
    public ApiResponse<String> delete(@PathVariable Integer employeeId) {
        employeeService.delete(employeeId);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000102002")
    @Operation(summary = "查詢部門員工基本資料", description = "查詢部門員工基本資料API")
    @GetMapping(path = "/query/{employeeId}")
    public ApiResponse<QueryEmployeeVO> query(@PathVariable Integer employeeId) {
        QueryEmployeeVO queryEmployeeVO = employeeService.query(employeeId);
        return new ApiResponse<>(queryEmployeeVO);
    }

    @RequiresPermissions("000102001")
    @Operation(summary = "根據部門ID查詢該部門員工", description = "根據部門ID查詢該部門員工API")
    @GetMapping(path = "/queryByDepartmentId/{departmentId}")
    public ApiResponse<List<QueryEmployeeVO>> queryByDepartmentId(@PathVariable Integer departmentId) {
        List<QueryEmployeeVO> queryEmployeeVOs = employeeService.queryByDepartmentId(departmentId);
        return new ApiResponse<>(queryEmployeeVOs);
    }

    @RequiresPermissions("000102001")
    @Operation(summary = "查詢所有員工資料", description = "獲取所有員工資料API")
    @GetMapping(path = "/getAllEmployee")
    public ApiResponse<List<QueryEmployeeVO>> getAllEmployee() {
        List<QueryEmployeeVO> queryEmployeeVOs = employeeService.getAllEmployee();
        return new ApiResponse<>(queryEmployeeVOs);
    }

    @Operation(summary = "取得員工清單", description = "取得員工清單API，提供給下拉選單使用")
    @GetMapping("/getEnumList")
    public ApiResponse<List<DropDownVo<Integer>>> getEnumList() {
        List<DropDownVo<Integer>> employees = employeeService.list().stream()
                .map(employee -> new DropDownVo<>(employee.getNickName(), employee.getId(), employee.getDepartmentId()))
                .toList();
        return new ApiResponse<>(employees);
    }
}
