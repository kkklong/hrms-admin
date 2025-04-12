package com.hrms.service;

import com.hrms.entity.Employee;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.bo.LoginBO;
import com.hrms.util.EncryptionUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
        String password;
        try {
            password = EncryptionUtils.encryptWithMD5(loginBO.getPassword());
        } catch (NoSuchAlgorithmException e) {
            log.error("md5加密失敗", e);
            throw new ServiceException(ErrorCode.GENERAL_ERROR);
        }

        Employee employee = employeeService.getByAccount(loginBO.getAccount());
        if (employee == null)
            throw new ServiceException(ErrorCode.FAILURE, "無對應的員工帳號，請確定帳號是否輸入正確!");

        //先這樣實作，之後確定密碼加密機制後再做修改!!
        if (employee.getPassword() == null || loginBO.getPassword() == null || !employee.getPassword().equals(password))
            throw new ServiceException(ErrorCode.FAILURE, "密碼錯誤!");


        //先生成假的token回去，以後產生真正的token再補上
        return "qewtc4tgy54y5hyhu6nuju";
    }
}
