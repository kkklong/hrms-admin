package com.hrms.controller;

import com.hrms.common.ApiResponse;
import com.hrms.entity.Department;
import com.hrms.entity.mapstruct.DepartmentMapper;
import com.hrms.model.bo.DepartmentBO;
import com.hrms.model.vo.DepartmentVO;
import com.hrms.model.vo.DropDownVo;
import com.hrms.service.DepartmentService;
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
 * @since 2025-04-13
 */
@Slf4j
@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Resource
    private DepartmentService departmentService;

    @RequiresPermissions("000101003")
    @Operation(summary = "創建部門資料", description = "創建一個新的部門API")
    @PostMapping(path = "/create")
    public ApiResponse<String> create(@Valid @RequestBody DepartmentBO departmentBO) {
        departmentService.create(departmentBO);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000101002")
    @Operation(summary = "更新部門資料", description = "更新部門資料API")
    @PostMapping(path = "/update")
    public ApiResponse<String> update(@Valid @RequestBody DepartmentBO departmentBO) {
        departmentService.update(departmentBO);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000101004")
    @Operation(summary = "刪除部門資料", description = "刪除一個部門API")
    @PostMapping(path = "/delete/{departmentId}")
    public ApiResponse<String> delete(@PathVariable Integer departmentId) {
        departmentService.deleteById(departmentId);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000101001")
    @Operation(summary = "查詢部門資料", description = "查詢部門資料API")
    @GetMapping("/query")
    public ApiResponse<List<DepartmentVO>> query() {
        List<Department> departments = departmentService.list();
        List<DepartmentVO> departmentVOs = departments.stream()
                .map(DepartmentMapper.INSTANCE::DepartmentToDepartmentVo)
                .toList();
        return new ApiResponse<>(departmentVOs);
    }

    @Operation(summary = "取得部門清單", description = "取得部門清單API，提供給下拉選單使用")
    @GetMapping("/getEnumList")
    public ApiResponse<List<DropDownVo<Integer>>> getEnumList() {
        List<DropDownVo<Integer>> departments = departmentService.list().stream()
                .map(department -> new DropDownVo<>(department.getDepartmentName(), department.getId()))
                .toList();
        return new ApiResponse<>(departments);
    }
}
