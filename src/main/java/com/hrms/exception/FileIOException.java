package com.hrms.exception;

import com.hrms.enums.ErrorCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(chain = true)
public class FileIOException extends RuntimeException {

    private ErrorCode errorCode;
    List<String> errorFields = new ArrayList<>();

    public FileIOException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public FileIOException(ErrorCode errorCode, List<String> errorFields) {
        super(errorCode.getMessage());
        this.errorCode  = errorCode;
        this.errorFields = errorFields;
    }
}
