package com.hrms.controller;

import com.hrms.common.ApiResponse;
import com.hrms.model.bo.LoginBO;
import com.hrms.model.vo.LoginVO;
import com.hrms.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
