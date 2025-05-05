package com.hrms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hrms.common.ApiResponse;
import com.hrms.entity.Role;
import com.hrms.entity.mapstruct.RoleMapper;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.bo.CreateRoleBO;
import com.hrms.model.bo.UpdateRoleBO;
import com.hrms.model.vo.DropDownVo;
import com.hrms.model.vo.RoleVO;
import com.hrms.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @RequiresPermissions("000001003")
    @Operation(summary = "創建角色權限", description = "創建一個角色API")
    @PostMapping(path = "/create")
    public ApiResponse<String> create(@Valid @RequestBody CreateRoleBO createRoleBO) {
        Role role = RoleMapper.INSTANCE.toRole(createRoleBO);
        roleService.save(role);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000001004")
    @Operation(summary = "修改角色權限", description = "修改一個角色權限API")
    @PostMapping(path = "/update")
    public ApiResponse<String> update(@Valid @RequestBody UpdateRoleBO updateRoleBO) {
        Role role = RoleMapper.INSTANCE.toRole(updateRoleBO);
        if (ObjectUtils.isEmpty(roleService.getById(role.getId()))) {
            throw new ServiceException(ErrorCode.ROLE_IS_NOT_NULL);
        }
        roleService.updateById(role);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000001005")
    @Operation(summary = "刪除角色權限", description = "刪除一個角色權限API")
    @PostMapping(path = "/delete/{roleId}")
    public ApiResponse<String> delete(@PathVariable Integer roleId) {
        if (roleService.removeById(roleId)) {
            return new ApiResponse<>();
        }
        return new ApiResponse<>(ErrorCode.ROLE_IS_NOT_NULL);
    }

    @RequiresPermissions("000001002")
    @Operation(summary = "查詢角色權限", description = "查詢一個角色權限API")
    @GetMapping(path = "/query/{roleId}")
    public ApiResponse<RoleVO> queryRoleById(@PathVariable Integer roleId) {
        return new ApiResponse<>(roleService.query(roleId));
    }

    @RequiresPermissions("000001001")
    @Operation(summary = "查詢角色權限列表", description = "查詢角色權限列表API")
    @GetMapping(path = "/query")
    public ApiResponse<List<RoleVO>> query() {
        List<Role> roles =roleService.getBaseMapper().selectList(new QueryWrapper<>());
        List<RoleVO> roleVOs = roles.stream().map(RoleMapper.INSTANCE::toRoleVO).toList();
        return new ApiResponse<>(roleVOs);
    }

    @Operation(summary = "取得角色清單", description = "取得角色清單API，提供給下拉選單使用")
    @GetMapping("/getEnumList")
    public ApiResponse<List<DropDownVo<Integer>>> getEnumList() {
        List<DropDownVo<Integer>> roles = roleService.list().stream()
                .map(role -> new DropDownVo<>(role.getRoleName(), role.getId()))
                .toList();
        return new ApiResponse<>(roles);
    }

}
