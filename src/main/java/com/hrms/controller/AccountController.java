package com.hrms.controller;

import com.hrms.common.ApiResponse;
import com.hrms.model.UserInfo;
import com.hrms.model.bo.LoginBO;
import com.hrms.model.vo.LoginVO;
import com.hrms.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@Tag(name = "使用者帳號", description = "使用者帳號API")
@Slf4j
public class AccountController {

    @Resource
    private LoginService loginService;

    @Operation(summary = "用戶登入", description = "用戶登入API")
    @PostMapping(path = "/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginBO loginBO) {
        String token = loginService.login(loginBO);
        LoginVO loginVo = new LoginVO();
        loginVo.setAccessToken(token);
        return new ApiResponse<>(loginVo);
    }

    @Operation(summary = "用戶登出", description = "用戶登出API")
    @PostMapping(path = "/logout")
    public ApiResponse logout() {
        loginService.logout();
        return new ApiResponse<>("登出成功");
    }

    @Operation(summary = "取得當前登入者的訊息", description = "取得當前登入者的訊息API")
    @GetMapping(path = "/currentEmployee")
    public ApiResponse<UserInfo> getCurrentEmployee() {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        return new ApiResponse<>(userInfo);
    }
}
