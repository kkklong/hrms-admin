package com.hrms.controller;

import com.hrms.common.ApiResponse;
import com.hrms.entity.Config;
import com.hrms.enums.*;
import com.hrms.model.vo.DropDownVo;
import com.hrms.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/enum")
@Tag(name = "列舉資料", description = "列舉資料API")
@Slf4j
public class EnumController {

    @Resource
    ConfigService configService;

    // ---- employee ----
    @Operation(summary = "取得員工狀態列表", description = "取得員工狀態列表API")
    @GetMapping(path = "/getEmployeeStatus")
    public ApiResponse<List<DropDownVo<Byte>>> getEmployeeStatus() {
        List<DropDownVo<Byte>> data = Stream.of(EmployeeStatus.values())
                .map(status -> new DropDownVo<>(status.getName(), status.getValue()))
                .collect(Collectors.toList());
        return new ApiResponse<>(data);
    }

    // ---- notice ----
        @Operation(summary = "取得公告狀態列表", description = "取得公告狀態列表API")
    @GetMapping(path = "/getNoticeStatus")
    public ApiResponse<List<DropDownVo<Byte>>> getNoticeStatus() {
        List<DropDownVo<Byte>> data = Stream.of(NoticeStatus.values())
                .map(stage -> new DropDownVo<>(stage.getName(), stage.getValue()))
                .collect(Collectors.toList());
        return new ApiResponse<>(data);
    }

    @Operation(summary = "取得公告類型列表", description = "取得公告類型列表API")
    @GetMapping(path = "/getNoticeType")
    public ApiResponse<List<DropDownVo<String>>> getNoticeType() {
        List<DropDownVo<String>> data = Stream.of(NoticeType.values())
                .map(stage -> new DropDownVo<>(stage.getValue(), stage.getValue()))
                .collect(Collectors.toList());
        return new ApiResponse<>(data);
    }

    // ----companyType ----
    @Operation(summary = "取得公司列表", description = "取得公司列表 API")
    @GetMapping(path = "/getCompanyType")
    public ApiResponse<List<DropDownVo<Byte>>> getCompanyType() {
        List<DropDownVo<Byte>> data = Stream.of(CompanyType.values())
                .map(company -> new DropDownVo<>(company.getName(), company.getValue()))
                .collect(Collectors.toList());
        return new ApiResponse<>(data);
    }

    // ---- shiftType----

    @Operation(summary = "取得排班類型列表", description = "取得排班類型列表API")
    @GetMapping(path = "/getShiftType")
    public ApiResponse<List<DropDownVo<String>>> getShiftType() {
        List<Config> shiftTypes = configService.getShiftType();
        List<DropDownVo<String>> data = shiftTypes.stream()
                .map(shiftType -> new DropDownVo<>(shiftType.getName(), shiftType.getConfigKey()))
                .collect(Collectors.toList());
        return new ApiResponse<>(data);
    }

//    @Operation(summary = "取得假單狀態列表", description = "取得假單狀態列表API")
//    @GetMapping(path = "/getLeaveRecordStatus")
//    public ApiResponse<List<DropDownVo<Byte>>> getLeaveRecordStatus() {
//        List<DropDownVo<Byte>> data = Stream.of(LeaveRecordStatus.values())
//                .map(status -> new DropDownVo<>(status.getName(), status.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得加班狀態列表", description = "取得加班狀態列表API")
//    @GetMapping(path = "/getOvertimeRecordStatus")
//    public ApiResponse<List<DropDownVo<Byte>>> getOvertimeRecordStatus() {
//        List<DropDownVo<Byte>> data = Stream.of(OvertimeRecordStatus.values())
//                .map(status -> new DropDownVo<>(status.getName(), status.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得請假流程狀態列表", description = "取得請假流程狀態列表API")
//    @GetMapping(path = "/getLeaveRecordApprovalStage")
//    public ApiResponse<List<DropDownVo<Byte>>> getLeaveRecordApprovalStage() {
//        List<DropDownVo<Byte>> data = Stream.of(LeaveRecordApprovalStage.values())
//                .map(stage -> new DropDownVo<>(stage.getName(), stage.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得加班流程狀態列表", description = "取得加班流程狀態列表API")
//    @GetMapping(path = "/getOvertimeRecordApprovalStage")
//    public ApiResponse<List<DropDownVo<Byte>>> getOvertimeRecordApprovalStage() {
//        List<DropDownVo<Byte>> data = Stream.of(OvertimeRecordApprovalStage.values())
//                .map(stage -> new DropDownVo<>(stage.getName(), stage.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得記薪標準列表", description = "取得記薪標準列表API")
//    @GetMapping(path = "/getLeaveSpecialRecordSalaryStandard")
//    public ApiResponse<List<DropDownVo<String>>> getLeaveSpecialRecordSalaryStandard() {
//        List<DropDownVo<String>> data = Stream.of(LeaveSpecialRecordSalaryStandard.values())
//                .map(stage -> new DropDownVo<>(stage.getName(), stage.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得計算期間類型列表", description = "取得計算期間類型列表API")
//    @GetMapping(path = "/getLeaveSpecialRecordCalculationPeriod")
//    public ApiResponse<List<DropDownVo<String>>> getLeaveSpecialRecordCalculationPeriod() {
//        List<DropDownVo<String>> data = Stream.of(LeaveSpecialRecordCalculationPeriod.values())
//                .map(stage -> new DropDownVo<>(stage.getName(), stage.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得假別列表", description = "取得假別列表API")
//    @GetMapping(path = "/getLeaveType")
//    public ApiResponse<List<DropDownVo<String>>> getLeaveType() {
//        List<DropDownVo<String>> data = Stream.of(LeaveType.values())
//                .map(leaveType -> new DropDownVo<>(leaveType.getChineseName(), leaveType.getLeaveType()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得非自動排程的假別列表", description = "取得非自動排程的假別列表API")
//    @GetMapping(path = "/getNonAutoScheduledLeaveType")
//    public ApiResponse<List<DropDownVo<String>>> getNonAutoScheduledLeaveTypes() {
//        List<DropDownVo<String>> data = Stream.of(LeaveType.values())
//                .filter(leaveType -> !leaveType.isAutoScheduled())
//                .map(leaveType -> new DropDownVo<>(leaveType.getChineseName(), leaveType.getLeaveType()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得公告狀態列表", description = "取得公告狀態列表API")
//    @GetMapping(path = "/getNoticeStatus")
//    public ApiResponse<List<DropDownVo<Byte>>> getNoticeStatus() {
//        List<DropDownVo<Byte>> data = Stream.of(NoticeStatus.values())
//                .map(stage -> new DropDownVo<>(stage.getName(), stage.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得公告類型列表", description = "取得公告類型列表API")
//    @GetMapping(path = "/getNoticeType")
//    public ApiResponse<List<DropDownVo<String>>> getNoticeType() {
//        List<DropDownVo<String>> data = Stream.of(NoticeType.values())
//                .map(stage -> new DropDownVo<>(stage.getValue(), stage.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//
//    @Operation(summary = "取得出勤狀態列表", description = "取得出勤狀態列表 API")
//    @GetMapping(path = "/getAttendanceRecordStatus")
//    public ApiResponse<List<DropDownVo<Integer>>> getAttendanceRecordStatus() {
//        List<DropDownVo<Integer>> data = Stream.of(AttendanceRecordStatus.values())
//                .map(status -> new DropDownVo<>(status.getName(), status.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得排班狀態列表", description = "取得排班狀態列表 API")
//    @GetMapping(path = "/getShiftScheduleStatus")
//    public ApiResponse<List<DropDownVo<Byte>>> getShiftScheduleStatus() {
//        List<DropDownVo<Byte>> data = Stream.of(ShiftScheduleStatus.values())
//                .filter(status -> status != ShiftScheduleStatus.DEFAULT_HOLIDAY) // 過濾掉 DEFAULT_HOLIDAY
//                .map(status -> new DropDownVo<>(status.getName(), status.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得班別時段列表", description = "取得班別時段列表 API")
//    @GetMapping(path = "/getTimeSlot")
//    public ApiResponse<List<DropDownVo<String>>> getTimeSlot() {
//        List<DropDownVo<String>> data = Stream.of(TimeSlot.values())
//                .map(slot -> new DropDownVo<>(slot.getName(), slot.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
//
//    @Operation(summary = "取得公司列表", description = "取得公司列表 API")
//    @GetMapping(path = "/getCompanyType")
//    public ApiResponse<List<DropDownVo<Byte>>> getCompanyType() {
//        List<DropDownVo<Byte>> data = Stream.of(CompanyType.values())
//                .map(company -> new DropDownVo<>(company.getName(), company.getValue()))
//                .collect(Collectors.toList());
//        return new ApiResponse<>(data);
//    }
}
