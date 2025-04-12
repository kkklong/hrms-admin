package com.hrms.exception;

import com.hrms.enums.ErrorCode;
import lombok.Getter;
import org.apache.shiro.authc.AuthenticationException;

public class ShiroAuthenticationException extends AuthenticationException {
    @Getter
    private ErrorCode errorCode;

    public ShiroAuthenticationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ShiroAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
