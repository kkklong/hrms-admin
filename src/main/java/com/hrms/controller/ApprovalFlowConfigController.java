package com.hrms.controller;

import com.hrms.common.ApiResponse;
import com.hrms.entity.ApprovalFlowConfig;
import com.hrms.entity.mapstruct.ApprovalFlowConfigMapper;
import com.hrms.model.bo.ApprovalFlowConfigBO;
import com.hrms.model.vo.CompanyVO;
import com.hrms.model.vo.LeaveRecordApprovalStageVO;
import com.hrms.model.vo.ReviewIntervalVO;
import com.hrms.model.vo.ScopeTypeVO;
import com.hrms.service.ApprovalFlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 客製化簽核流程設定 前端控制器
 * </p>
 *
 * @author System
 * @since 2025-07-31
 */
@Slf4j
@RestController
@Tag(name = "審核流程設定", description = "客製化簽核流程設定API")
@RequestMapping("/approvalFlowConfig")
public class ApprovalFlowConfigController {
    @Resource
    private ApprovalFlowService approvalFlowService;

    @RequiresPermissions("000315002")
    @Operation(summary = "新增審核流程", description = "新增審核流程API")
    @PostMapping("/create")
    public ApiResponse<String> create(@Valid @RequestBody ApprovalFlowConfigBO approvalFlowConfigBO){
        approvalFlowService.upsertFlowConfig(approvalFlowConfigBO);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000315003")
    @Operation(summary = "更新審核流程", description = "更新審核流程API")
    @PostMapping("/update")
    public ApiResponse<String> update(@Valid @RequestBody ApprovalFlowConfigBO approvalFlowConfigBO){
        approvalFlowService.upsertFlowConfig(approvalFlowConfigBO);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000315001")
    @Operation(summary = "查詢審核流程設定", description = "查詢審核流程設定API")
    @PostMapping("/query")
    public ApiResponse<List<ApprovalFlowConfig>> query(){
        return new ApiResponse<>(approvalFlowService.list());
    }

    @RequiresPermissions("000315004")
    @Operation(summary = "刪除審核流程", description = "刪除審核流程設定API")
    @PostMapping("/delete/{id}")
    public ApiResponse<String> delete(@PathVariable Integer id){
        approvalFlowService.deleteById(id);
        return new ApiResponse<>();
    }

    @Operation(summary = "審核人VO", description = "審核人選擇器")
    @PostMapping("/reviewSelector")
    public ApiResponse<List<LeaveRecordApprovalStageVO>> reviewSelector(){
        return new ApiResponse<>(ApprovalFlowConfigMapper.INSTANCE.enumToLeaveRecordApprovalStageVOs());
    }

    @Operation(summary = "審核區間VO", description = "審核區間選擇器")
    @PostMapping("/reviewIntervalSelector")
    public ApiResponse<List<ReviewIntervalVO>> reviewIntervalSelector(){
        return new ApiResponse<>(ApprovalFlowConfigMapper.INSTANCE.enumToReviewIntervalVOs());
    }

    @Operation(summary = "審核類型VO", description = "審核類型選擇器")
    @PostMapping("/scopeTypeSelector")
    public ApiResponse<List<ScopeTypeVO>> scopeTypeSelector(){
        return new ApiResponse<>(ApprovalFlowConfigMapper.INSTANCE.enumToScopeTypeVOs());
    }

    @Operation(summary = "公司名稱VO", description = "公司名稱選擇器")
    @PostMapping("/companySelector")
    public ApiResponse<List<CompanyVO>> companySelector(){
        return new ApiResponse<>(ApprovalFlowConfigMapper.INSTANCE.enumToCompanyVOs());
    }
}
