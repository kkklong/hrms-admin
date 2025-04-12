package com.hrms.common;

import com.hrms.enums.ErrorCode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
    private List<String> errorFields;

    public ApiResponse() {
        this.code = BaseCode.SUCCESS.getCode();
        this.message = BaseCode.SUCCESS.getMessage();
    }

    public ApiResponse(T data) {
        this.code = BaseCode.SUCCESS.getCode();
        this.message = BaseCode.SUCCESS.getMessage();
        this.data = data;
    }

    public ApiResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ApiResponse(ErrorCode errorCode, T data) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = data;
    }

    enum BaseCode {
        SUCCESS(0, "Success");
        private final int code;
        private final String message;

        BaseCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

}
