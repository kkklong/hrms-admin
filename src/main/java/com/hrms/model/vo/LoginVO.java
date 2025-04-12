package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Schema(description = "前台用戶登入Vo")
@Accessors(chain = true)
public class LoginVO implements Serializable {
    @Schema(type = "String", description = "access token")
    private String accessToken;
}
