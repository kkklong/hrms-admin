package com.hrms.service;

import com.hrms.entity.Employee;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.UserInfo;
import com.hrms.model.bo.LoginBO;
import com.hrms.util.EncryptionUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@Transactional
public class LoginService {

    @Resource
    private EmployeeService employeeService;

    /**
     * 登入驗證
     */
    public String login(LoginBO loginBO) {

        Subject currentUser = SecurityUtils.getSubject();
        // 如果用戶已經登錄，先登出
        if (currentUser.isAuthenticated()) {
            currentUser.logout();
        }

        String password;
        try {
            password = EncryptionUtils.encryptWithMD5(loginBO.getPassword());
        } catch (NoSuchAlgorithmException e) {
            log.error("md5加密失敗", e);
            throw new ServiceException(ErrorCode.GENERAL_ERROR);
        }
        UsernamePasswordToken token = new UsernamePasswordToken(loginBO.getAccount(), password);
        currentUser.login(token);
        String userToken = (String) currentUser.getSession().getId();
        return userToken;
    }

    public void logout() {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
    }

    /**
     * 取得登入者資料
     */
    public UserInfo getCurrentUserInfo() {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            return (UserInfo) currentUser.getPrincipal();
        } else {
            return null;
        }
    }
}
