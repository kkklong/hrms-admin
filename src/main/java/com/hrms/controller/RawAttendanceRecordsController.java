package com.hrms.controller;

import com.hrms.common.ApiResponse;
import com.hrms.model.bo.DepartmentBO;
import com.hrms.model.bo.RawAttendanceRecordsBO;
import com.hrms.model.vo.RawAttendanceRecordsVO;
import com.hrms.service.RawAttendanceRecordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author System
 * @since 2024-10-05
 */
@Slf4j
@RestController
@RequestMapping("/rawAttendanceRecords")
@Tag(name = "打卡記錄", description = "打卡記錄API")
public class RawAttendanceRecordsController {

    @Resource
    public RawAttendanceRecordsService rawAttendanceRecordsService;

    @Operation(summary = "查詢全員打卡記錄", description = "查詢全員打卡記錄 API")
    @GetMapping("/query")
    public ApiResponse<List<RawAttendanceRecordsVO>> query(@Valid RawAttendanceRecordsBO rawAttendanceRecordsBO) {
        List<RawAttendanceRecordsVO> list = rawAttendanceRecordsService.query(rawAttendanceRecordsBO);
        return new ApiResponse<>(list);
    }

    // ---- 測試用dao ----
    @Operation(summary = "更新打卡記錄", description = "更新打卡記錄API")
    @PostMapping(path = "/update")
    public ApiResponse<String> update(@Valid @RequestBody RawAttendanceRecordsVO rawAttendanceRecordsVO) {
        rawAttendanceRecordsService.updaterawAttendance(rawAttendanceRecordsVO);
        return new ApiResponse<>();
    }

}
