package com.hrms.controller;


import com.hrms.common.ApiResponse;
import com.hrms.model.UserInfo;
import com.hrms.model.bo.ShiftAdjustmentApplyBo;
import com.hrms.model.bo.ShiftAdjustmentApprovalBo;
import com.hrms.model.bo.ShiftAdjustmentCancelBO;
import com.hrms.model.bo.ShiftAdjustmentRejectBo;
import com.hrms.model.vo.ShiftAdjustmentRequestVO;
import com.hrms.repository.ShiftAdjustmentRequestRepository;
import com.hrms.service.ShiftAdjustmentRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 班表調整申請單 (含審核暫鎖控制) 前端控制器
 * </p>
 *
 * @author System
 * @since 2025-11-06
 */
@Slf4j
@RestController
@Tag(name = "班表調整申請 API", description = "員工申請班表調整與主管審核操作")
@RequestMapping("/shiftAdjustmentRequest")
public class ShiftAdjustmentRequestController {

    @Resource
    private ShiftAdjustmentRequestService shiftAdjustmentService;
    @Resource
    private ShiftAdjustmentRequestRepository requestRepository;

    @RequiresPermissions("000316001")
    @Operation(summary = "發起班表調整申請")
    @PostMapping("/apply")
    public ApiResponse<Long> apply(@RequestBody ShiftAdjustmentApplyBo request) {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Long id = shiftAdjustmentService.apply(
                request.getShiftMap(),
                request.getReason(),
                userInfo.getId()
        );
        return new ApiResponse<>(id);
    }

    @RequiresPermissions("000317001")
    @Operation(summary = "班表調整審核")
    @PostMapping("/approve")
    public ApiResponse<?> approveStage(@RequestBody ShiftAdjustmentApprovalBo approvalBo) {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        shiftAdjustmentService.approveStage(
                approvalBo.getRequestId(),
                approvalBo.getStage(),
                userInfo.getId(),
                approvalBo.getRemark()
        );
        return new ApiResponse<>();
    }

    @RequiresPermissions("000317002")
    @Operation(summary = "班表調整駁回")
    @PostMapping("/reject")
    public ApiResponse<?> rejectStage(@RequestBody ShiftAdjustmentRejectBo rejectBo) {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        shiftAdjustmentService.rejectStage(
                rejectBo.getRequestId(),
                rejectBo.getStage(),
                userInfo.getId(),
                rejectBo.getReason()
        );
        return new ApiResponse<>("已駁回申請");
    }

    @RequiresPermissions("000317003")
    @Operation(summary = "查詢待審核的班表調整申請", description = "查詢待審核的班表調整申請（依登入者權限與階段）")
    @GetMapping("/getPendingShiftAdjustments")
    public ApiResponse<List<ShiftAdjustmentRequestVO>> getPendingShiftAdjustments() {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        List<ShiftAdjustmentRequestVO> pendingRequests = shiftAdjustmentService.getPendingShiftAdjustments(userInfo);
        return new ApiResponse<>(pendingRequests);
    }

    @RequiresPermissions("000316002")
    @Operation(summary = "查詢個人班表調整紀錄", description = "查詢當前登入者的所有班表調整紀錄API")
    @GetMapping("/currentEmployeeShiftAdjustments")
    public ApiResponse<List<ShiftAdjustmentRequestVO>> getCurrentEmployeeShiftAdjustments() {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        List<ShiftAdjustmentRequestVO> myRequests = shiftAdjustmentService.queryByApplicantId(userInfo.getId());
        return new ApiResponse<>(myRequests);
    }

    @RequiresPermissions("000316003")
    @Operation(summary = "取消班表調整申請", description = "取消班表調整申請API (僅限送審中狀態)")
    @PostMapping(path = "/cancel")
    public ApiResponse<String> cancelShiftAdjustment(@Valid @RequestBody ShiftAdjustmentCancelBO cancelBO) {
        shiftAdjustmentService.cancelShiftAdjustment(cancelBO);
        return new ApiResponse<>("取消成功");
    }

}