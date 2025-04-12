package com.hrms.exception;

import com.hrms.enums.ErrorCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class ServiceException extends RuntimeException {

    private final ErrorCode errorCode;
    private Object data;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServiceException(ErrorCode errorCode, Object data) {
        super(errorCode.getMessage());
        this.errorCode  = errorCode;
        this.data = data;
    }
}
