package com.hrms.model.bo;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用戶登入Bo")
public class LoginBO {

    @NotBlank(message = "account cannot be empty")
    @Schema(type = "String", description = "帳號")
    private String account;

    @NotBlank(message = "password cannot be empty")
    @Schema(type = "String", description = "密碼")
    private String password;

}
