package com.hrms.controller;

import com.hrms.common.ApiResponse;
import com.hrms.entity.Config;
import com.hrms.model.bo.ShiftSchedulesBO;
import com.hrms.model.bo.ShiftTypeBO;
import com.hrms.model.vo.ShiftSchedulePeriodVo;
import com.hrms.model.vo.ShiftSchedulesVO;
import com.hrms.model.vo.ShiftTypeVo;
import com.hrms.service.ConfigService;
import com.hrms.service.ShiftSchedulesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
@RequestMapping("/shiftSchedules")
public class ShiftSchedulesController {

    @Resource
    private ShiftSchedulesService shiftSchedulesService;
    @Resource
    private ConfigService configService;

    @Operation(summary = "取得班別及假日配置", description = "取得班別及假日配置API")
    @GetMapping(path = "/getShiftAndHolidayConfig")
    public ApiResponse<List<ShiftTypeVo>> getShiftAndHolidayConfig() {
        // 查詢 SHIFT_TYPE 和 HOLIDAY 配置
        List<Config> shiftTypes = configService.getShiftType();
        List<Config> holidays = configService.getHoliday();
        // 合併兩種類型的配置
        List<Config> configs = new ArrayList<>();
        configs.addAll(shiftTypes);
        configs.addAll(holidays);
        // 將查詢結果轉換為 ShiftTypeVo
        List<ShiftTypeVo> data = configs.stream()
                .map(config -> new ShiftTypeVo()
                        .setId(config.getId())
                        .setShiftName(config.getName())
                        .setShiftKey(config.getConfigKey())
                        .setStartTime(Optional.ofNullable(config.getConfigValue1()).map(LocalTime::parse).orElse(null))
                        .setEndTime(Optional.ofNullable(config.getConfigValue2()).map(LocalTime::parse).orElse(null))
                        .setLunchStartTime(Optional.ofNullable(config.getConfigValue3()).map(LocalTime::parse).orElse(null))
                        .setLunchEndTime(Optional.ofNullable(config.getConfigValue4()).map(LocalTime::parse).orElse(null))
                        .setFlexibleWork(Optional.ofNullable(config.getConfigValue()).map(Boolean::parseBoolean).orElse(false))
                        .setShiftColorCode(config.getConfigValue5())
                        .setTimeSlot(config.getConfigValue6())
                )
                .toList();
        return new ApiResponse<>(data);
    }

    @RequiresPermissions("000311001")
    @Operation(summary = "儲存班別配置", description = "儲存班別配置API")
    @PostMapping(path = "/saveShiftType")
    public ApiResponse<String> saveShiftType(@Valid @RequestBody ShiftTypeBO shiftTypeBO) {
        shiftSchedulesService.saveShiftType(shiftTypeBO);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000405002")
    @Operation(summary = "載入年班表", description = "從CSV文件中載入年班表")
    @PostMapping(path = "/loadShiftSchedule", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> loadShiftSchedule(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        shiftSchedulesService.loadShiftScheduleFromCSV(file);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000405003")
    @Operation(summary = "根據區間及部門查詢排班資料", description = "根據起始和結束日期及部門查詢排班資料")
    @GetMapping(path = "/queryByMonthAndDepartment")
    public ApiResponse<List<ShiftSchedulesVO>> getShiftSchedulesByMonth(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "departmentId", required = false) Integer departmentId) {
        List<ShiftSchedulesVO> shiftSchedules = shiftSchedulesService.queryByPeriodAndDepartment(startDate, endDate, departmentId);

        return new ApiResponse<>(shiftSchedules);
    }

    @RequiresPermissions("000308001")
    @Operation(summary = "區間查詢", description = "查詢區間內的兩週起訖日期")
    @GetMapping("/getShiftSchedulePeriods")
    public ApiResponse<List<ShiftSchedulePeriodVo>> getShiftSchedulePeriods(
            @Schema(description = "起始日期", example = "2024-10-06") @RequestParam("startDate") LocalDate startDate,
            @Schema(description = "結束日期", example = "2024-10-07") @RequestParam("endDate") LocalDate endDate) {
        List<ShiftSchedulePeriodVo> periods = shiftSchedulesService.getShiftSchedulePeriods(startDate, endDate);
        return new ApiResponse<>(periods);
    }

    @RequiresPermissions("000308002")
    @Operation(summary = "儲存員工排班", description = "儲存員工排班API")
    @PostMapping(path = "/savePersonalShiftSchedules")
    public ApiResponse<String> savePersonalShiftSchedules(@Valid @RequestBody List<ShiftSchedulesBO> shiftSchedulesBOList) {
        shiftSchedulesService.savePersonalShiftSchedules(shiftSchedulesBOList);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000309004")
    @Operation(summary = "手動調整排班", description = "手動調整排班API")
    @PostMapping(path = "/manuallyAdjustShiftSchedules")
    public ApiResponse<String> manuallyAdjustShiftSchedules(@Valid @RequestBody List<ShiftSchedulesBO> shiftSchedulesBOList) {
        shiftSchedulesService.manuallyAdjustShiftSchedules(shiftSchedulesBOList);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000309002")
    @Operation(summary = "排班衝突檢測", description = "排班衝突檢測API")
    @PostMapping(path = "/checkShiftScheduleConflicts")
    public ApiResponse<String> checkShiftScheduleConflicts(@RequestParam("departmentId") int departmentId) {
        shiftSchedulesService.checkShiftScheduleConflicts(departmentId);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000309005")
    @Operation(summary = "關閉下個月班表", description = "關閉下個月班表API")
    @PostMapping(path = "/closeNextMonthShiftSchedules")
    public ApiResponse<String> closeNextMonthShiftSchedules(@RequestParam("departmentId") int departmentId) {
        shiftSchedulesService.closeNextMonthShiftSchedules(departmentId);
        return new ApiResponse<>();
    }
}
