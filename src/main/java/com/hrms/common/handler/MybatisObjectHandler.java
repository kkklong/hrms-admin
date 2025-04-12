package com.hrms.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.hrms.model.UserInfo;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;

import java.time.LocalDateTime;

public class MybatisObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createdDate", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updatedDate", LocalDateTime.class, now);
        //等權限表產出後這需要再改，目前先這樣
//        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        //排程檢查用
//        String accountName = (userInfo != null) ? userInfo.getAccount() : "admin";
        //當排程沒有登入者時，默認為admin
        String accountName = getCurrentAccountName();
        this.strictInsertFill(metaObject, "createdId", String.class, accountName);
        this.strictUpdateFill(metaObject, "updatedId", String.class, accountName);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictUpdateFill(metaObject, "updatedDate", LocalDateTime.class, now);
        //等權限表產出後這需要再改，目前先這樣
//        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        //排程檢查用
//        String accountName = (userInfo != null) ? userInfo.getAccount() : "admin";
        //當排程沒有登入者時，默認為admin
        String accountName = getCurrentAccountName();
        this.strictUpdateFill(metaObject, "updatedId", String.class, accountName);
    }

    private String getCurrentAccountName() {
        try {
            UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
            if (userInfo != null) {
                return userInfo.getAccount();
            }
        } catch (UnavailableSecurityManagerException e) {
            return "admin";
        }
        return "admin";
    }

}
