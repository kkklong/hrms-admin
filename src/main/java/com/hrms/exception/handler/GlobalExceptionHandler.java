package com.hrms.exception.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.hrms.common.ApiResponse;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.FileIOException;
import com.hrms.exception.ServiceException;
import com.hrms.exception.ShiroAuthenticationException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 驗證錯誤exception
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse> globalMethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: ", ex);
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorFields = (Objects.nonNull(bindingResult) && bindingResult.hasErrors()) ? bindingResult.getFieldErrors().stream().filter(Objects::nonNull).map(e -> e.getField() + ":" + e.getDefaultMessage()).collect(Collectors.toList()) : null;

        ApiResponse apiResponse = new ApiResponse<>(ErrorCode.INVALID_PARAMETER);
        if (!Objects.isNull(errorFields)) {
            apiResponse.setErrorFields(errorFields);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    /**
     * @param ex
     * @return
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResponse> globalExceptionHandler(Exception ex) {
        log.error(ex.toString(), ex);
        return new ResponseEntity<>(new ApiResponse<>(ErrorCode.GENERAL_ERROR), HttpStatus.OK);
    }

    /**
     * 服務發生例外
     *
     * @param e
     * @return
     */
    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<ApiResponse> globalExceptionHandler(ServiceException e) {
        log.error("ServiceException: {} {}", e.getErrorCode().getCode(), e.getMessage(), e);
        if (Objects.nonNull(e.getData())) {
            ApiResponse response = new ApiResponse();
            response.setCode(e.getErrorCode().getCode());
            response.setMessage(e.getMessage());
            response.setErrorFields(Arrays.asList(e.getData().toString()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse<>(e.getErrorCode()), HttpStatus.OK);
    }

    /**
     * 驗證例外
     */
    @ExceptionHandler({ShiroAuthenticationException.class})
    public ResponseEntity<ApiResponse> globalAuthExceptionHandler(ShiroAuthenticationException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getErrorCode()), HttpStatus.OK);
    }

    /**
     * SQL例外
     */
    @ExceptionHandler({SQLException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ApiResponse> globalSQLExceptionHandler(SQLException e) {
        log.error("SQLException or DataIntegrityViolationException: {}", e.getMessage(), e);
        return new ResponseEntity<>(new ApiResponse<>(ErrorCode.DATABASE_ERROR), HttpStatus.OK);
    }

    /**
     * 判斷前端物件參數型態
     *
     * @param req
     * @param ex
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> invalidFormatHandler(HttpServletRequest req, HttpMessageNotReadableException ex) throws IOException {
        log.error(ex.getMessage());
        ApiResponse apiResponse = new ApiResponse<>(ErrorCode.PARAMETER_TYPE_ERROE);
        List<String> errorFields = new ArrayList<>();

        final Throwable cause = ex.getCause() == null ? ex : ex.getCause();
        if (cause instanceof JsonParseException) {
            JsonParseException jpe = (JsonParseException) cause;
            errorFields.add(jpe.getProcessor().getCurrentName());
            apiResponse.setErrorFields(errorFields);
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ExceptionHandler({MessagingException.class})
    public ResponseEntity<ApiResponse> globalMessagingExceptionHandler(MessagingException e) {
        log.error("MessagingException: {}", e.getMessage(), e);
        return new ResponseEntity<>(new ApiResponse<>(ErrorCode.EMAIL_SENDING_ERROR), HttpStatus.OK);
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<ApiResponse> globalIOExceptionHandler(IOException e) {
        log.error("IOException: {}", e.getMessage(), e);
        return new ResponseEntity<>(new ApiResponse<>(ErrorCode.FILE_UPLOAD_ERROR), HttpStatus.OK);
    }


    /**
     * 無權限操作之例外
     */
    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<ApiResponse> unauthorizedExceptionHandler(UnauthorizedException e) {
        log.info("UnauthorizedException: {} {}", e.getCause().getMessage(), e);
        return new ResponseEntity<>(new ApiResponse<>(ErrorCode.NO_PERMISSION), HttpStatus.OK);
    }

    /**
     * 參數類型錯誤
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info("MethodArgumentTypeMismatchException: {} {}", e.getCause().getMessage(), e);
        return new ResponseEntity<>(new ApiResponse<>(ErrorCode.INVALID_PARAMETER), HttpStatus.OK);
    }

    /**
     * 檔案IO服務發生例外
     *
     * @param e
     * @return
     */
    @ExceptionHandler({FileIOException.class})
    public ResponseEntity<ApiResponse> globalFileIOExceptionHandler(FileIOException e) {
        log.error("ServiceException: {} {}", e.getErrorCode().getCode(), e.getMessage(), e);

        ApiResponse apiResponse = new ApiResponse<>(e.getErrorCode());

        if (!Objects.isNull(e.getErrorFields())) {
            apiResponse.setErrorFields(e.getErrorFields());
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
