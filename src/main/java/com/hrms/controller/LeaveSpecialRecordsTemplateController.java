package com.hrms.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hrms.common.ApiResponse;
import com.hrms.entity.LeaveSpecialRecordsTemplate;
import com.hrms.entity.mapstruct.LeaveSpecialRecordsTemplateMapper;
import com.hrms.model.bo.LeaveSpecialRecordsTemplateBO;
import com.hrms.model.vo.LeaveSpecialRecordsTemplateVO;
import com.hrms.service.LeaveSpecialRecordsTemplateService;
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
 *  前端控制器
 * </p>
 *
 * @author System
 * @since 2024-08-02
 */
@Slf4j
@RestController
@RequestMapping("/leaveSpecialRecordsTemplate")
@Tag(name = "預設假別設定", description = "預設假別設定API")
public class LeaveSpecialRecordsTemplateController {
    @Resource
    private LeaveSpecialRecordsTemplateService leaveSpecialRecordsTemplateService;

    @RequiresPermissions("000301001")
    @Operation(summary = "查詢預設假別設定表", description = "查詢預設假別設定表API")
    @GetMapping(path = "/queryLeaveSpecialRecordsTemplate")
    public ApiResponse<List<LeaveSpecialRecordsTemplateVO>> queryEaveSpecialRecordsTemplate() {
        return new ApiResponse<>(leaveSpecialRecordsTemplateService.getLeaveSpecialRecordsTemplate());
    }

    @RequiresPermissions("000301002")
    @Operation(summary = "更新預設假別設定", description = "更新預設假設定API")
    @PostMapping(path = "/updateLeaveSpecialRecordsTemplate")
    public ApiResponse<String> updateEaveSpecialRecordsTemplate(@Valid @RequestBody LeaveSpecialRecordsTemplateBO leaveSpecialRecordsTemplateBO) {
        LeaveSpecialRecordsTemplate leaveSpecialRecordsTemplate = LeaveSpecialRecordsTemplateMapper.INSTANCE.toLeaveSpecialRecordsTemplate(leaveSpecialRecordsTemplateBO);
        UpdateWrapper<LeaveSpecialRecordsTemplate> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", leaveSpecialRecordsTemplateBO.getId());
        leaveSpecialRecordsTemplateService.update(leaveSpecialRecordsTemplate, updateWrapper);
        return new ApiResponse<>();
    }

}
