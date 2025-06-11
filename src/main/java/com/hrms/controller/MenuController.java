package com.hrms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hrms.common.ApiResponse;
import com.hrms.entity.Menu;
import com.hrms.entity.mapstruct.MenuMapper;
import com.hrms.model.RolePermission;
import com.hrms.model.UserInfo;
import com.hrms.model.vo.MenuRolesVO;
import com.hrms.model.vo.MenuVO;
import com.hrms.service.EmployeeService;
import com.hrms.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/menu")
@Tag(name = "選單", description = "選單API")
@Slf4j
public class MenuController {

    @Resource
    private MenuService menuService;

    @Resource
    private EmployeeService employeeService;

    @Operation(summary = "選單初始化", description = "選單初始化API")
    @GetMapping(path = "")
    public ApiResponse<List<MenuVO>> menu() {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        RolePermission rolePermission = employeeService.getRolePermission(userInfo.getAccount());
        String[] codes  = rolePermission.getMenuPermission().split(",");
        Set<String> userPermissions = new HashSet<>(Arrays.asList(codes));

        List<Menu> menu = menuService.getBaseMapper().selectList(new QueryWrapper<>());
        List<MenuVO> menuVos = new ArrayList<>();
        menu.forEach(e -> {
            if (StringUtils.isBlank(e.getSuperCode()) && userPermissions.contains(e.getCode())) {
                MenuVO menuVO = new MenuVO();
                menuVO.setCode(e.getCode());
                menuVO.setText(e.getText());
                List<Map<String,String>> data = new ArrayList<>();
                menu.forEach(k -> {
                    if (e.getCode().equals(k.getSuperCode()) && userPermissions.contains(k.getCode())) {
                        Map<String, String> dataMap = new HashMap<>();
                        dataMap.put("code",k.getCode());
                        dataMap.put("text",k.getText());
                        data.add(dataMap);
                    }
                });
                menuVO.setData(data);
                menuVos.add(menuVO);
            }
        });
        return new ApiResponse<>(menuVos);
    }

    @RequiresPermissions("000001006")
    @Operation(summary = "權限選單", description = "權限選單api")
    @GetMapping(path = "/menuRoles")
    public ApiResponse<List<MenuRolesVO>> menuRoles() {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("code");
        List<Menu> menu = menuService.getBaseMapper().selectList(queryWrapper);
        List<MenuRolesVO> menuVos = menu.stream()
                .map(MenuMapper.INSTANCE::toMenuRolesVO)
                .toList();
        return new ApiResponse<>(menuVos);
    }

}
