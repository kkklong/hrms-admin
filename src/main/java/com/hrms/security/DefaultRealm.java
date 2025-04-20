package com.hrms.security;

import com.hrms.entity.Employee;
import com.hrms.enums.EmployeeStatus;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ShiroAuthenticationException;
import com.hrms.model.RolePermission;
import com.hrms.model.UserInfo;
import com.hrms.service.EmployeeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

@Slf4j
public class DefaultRealm extends AuthorizingRealm {
    @Resource
    private EmployeeService employeeService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //獲取登錄用戶名
        UserInfo userInfo = (UserInfo) principals.getPrimaryPrincipal();

        RolePermission rolePermission = employeeService.getRolePermission(userInfo.getAccount());
        //添加角色和權限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        //添加角色
        simpleAuthorizationInfo.addRole(rolePermission.getRoleName());
        String[] permissions = rolePermission.getMenuPermission().split(",");
        //添加權限
        for (String permission : permissions) {
            simpleAuthorizationInfo.addStringPermission(permission);
        }

        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String account = upToken.getUsername();
        String password = new String(upToken.getPassword());

        Employee employee = employeeService.getByAccount(account);

        if (employee == null) {
            throw new ShiroAuthenticationException(ErrorCode.EMPLOYEE_ERROR);
        }

        if (employee.getPassword() == null || !employee.getPassword().equals(password)) {
            throw new ShiroAuthenticationException(ErrorCode.PASSWORD_ERROR);
        }

        if (!employee.getStatus().equals(EmployeeStatus.ACTIVE.getValue())) {
            throw new ShiroAuthenticationException(ErrorCode.NOT_CURRENT_EMPLOYEE_ERROR);
        }
        UserInfo userInfo = employeeService.getUserInfo(employee.getAccount());
        return new SimpleAuthenticationInfo(userInfo, employee.getPassword(), getName());
    }
}
