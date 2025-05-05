package com.hrms.model.bo;

import com.hrms.constant.VerificationRegexp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "用戶可修改的個人信息")
public class EditUserInfoBO {
    @Schema(type = "Integer",description = "部門員工id")
    private Integer id;

    @Pattern(regexp = VerificationRegexp.REGEXP_SKYPE, message = "skype格式不符")
    @Schema(type = "String", description = "skype賬號", pattern = VerificationRegexp.REGEXP_SKYPE)
    private String skype;

    @Pattern(regexp = VerificationRegexp.REGEXP_TELEGRAM, message = "telegram格式不符")
    @Schema(type = "String", description = "telegram賬號", pattern = VerificationRegexp.REGEXP_TELEGRAM)
    private String telegram;

    @NotBlank(message = "緊急聯絡人不能為空")
    @Schema(type = "String", description = "緊急聯絡人")
    private String emergencyContact;

    @NotBlank(message = "通訊地址不能為空")
    @Schema(type = "String", description = "通訊地址")
    private String address;

    @Schema(type = "String", description = "緊急聯絡人與員工的關係，例如父母、配偶、朋友等")
    private String relationship;

    @NotBlank(message = "緊急聯絡人電話不能為空")
    @Schema(type = "String", description = "緊急聯絡人電話")
    private String emergencyContactPhone;

    @Schema(type = "String", description = "緊急連絡人通訊地址")
    private String emergencyContactAddress;

    @Schema(type = "String", description = "戶籍地址")
    private String registeredAddress;
}
