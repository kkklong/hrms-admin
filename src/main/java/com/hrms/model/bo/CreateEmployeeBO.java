package com.hrms.model.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hrms.constant.VerificationRegexp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "部門員工")
public class CreateEmployeeBO {
    @Schema(type = "Integer",description = "部門員工id(為自動生成，不用輸入)")
    private Integer id;

    @NotBlank(message = "全名不能為空")
    @Schema(type = "String",description = "全名")
    private String fullName;

    @NotBlank(message = "別名不能為空")
    @Schema(type = "String",description = "別名")
    private String nickName;

    @NotBlank(message = "職位不能為空")
    @Schema(type = "String",description = "職位")
    private String position;

    @Digits(integer = 10, fraction = 1 ,message = "薪資僅能為数字")
    @DecimalMin(value = "0.0", inclusive = true)
    @Schema(type = "Long", description = "薪資")
    private Long salary;

    @NotBlank(message = "帳號不能為空")
    @Schema(type = "String",description = "帳號")
    private String account;

    @NotBlank(message = "密碼不能為空")
    @Schema(type = "String",description = "密碼")
    private String password;

    @NotNull(message = "部⻔ID不能為空")
    @Schema(type = "Integer",description = "部⻔ID")
    private Integer departmentId;

    @NotNull(message = "角色ID不能為空")
    @Schema(type = "Integer",description = "角色ID")
    private Integer roleId;

    @NotBlank(message = "性別不能為空")
    @Schema(type = "String",description = "性別", example = "男")
    private String gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    @Schema(type = "Date",description = "生日格式yyyy-MM-dd")
    private LocalDate birthday;

    @Pattern(regexp = VerificationRegexp.REGEXP_MOBILE_PHONE, message = "電話格式不符")
    @Schema(type = "String",description = "聯絡電話")
    private String phone;

    @Pattern(regexp = VerificationRegexp.REGEXP_EMAIL, message = "email格式不符")
    @Schema(type = "String",description = "信箱", pattern = VerificationRegexp.REGEXP_EMAIL,example = "test@hrms.com")
    private String email;

    @Pattern(regexp = VerificationRegexp.REGEXP_SKYPE, message = "skype格式不符")
    @Schema(type = "String",description = "skype賬號", pattern = VerificationRegexp.REGEXP_SKYPE)
    private String skype;

    @Pattern(regexp = VerificationRegexp.REGEXP_TELEGRAM, message = "telegram格式不符")
    @Schema(type = "String",description = "telegram賬號", pattern = VerificationRegexp.REGEXP_TELEGRAM)
    private String telegram;

    @NotBlank(message = "緊急聯絡人不能為空")
    @Schema(type = "String", description = "緊急聯絡人")
    private String emergencyContact;

    @NotBlank(message = "通訊地址")
    @Schema(type = "String", description = "通訊地址")
    private String address;

    @Schema(type = "String", description = "備註說明")
    private String remark;

    @Schema(type = "String", description = "所在樓層")
    private String floor;

    @Schema(type = "String", description = "座位編號")
    private String seatNumber;

    @Schema(type = "String", description = "緊急聯絡人與員工的關係，例如父母、配偶、朋友等")
    private String relationship;

    @Schema(type = "String", description = "緊急聯絡人電話")
    private String emergencyContactPhone;

    @NotNull(message = "入職時間不能為空，格式:yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    @Schema(type = "Date", description = "yyyy-MM-dd")
    private LocalDate entryDate;

    @Schema(type = "Integer", description = "加班是否換錢，預設是0，目前只有eg的java是1")
    private Integer overtimeType;

    @Schema(type = "BigDecimal", description = "勞保費用")
    private BigDecimal laborInsuranceFee;

    @Schema(type = "BigDecimal", description = "健保費用")
    private BigDecimal healthInsuranceFee;

    @Schema(type = "BigDecimal", description = "假日津貼")
    private BigDecimal holidayDutyAllowance;

    @Schema(type = "BigDecimal", description = "午班津貼")
    private BigDecimal afternoonShiftAllowance;

    @Schema(type = "BigDecimal", description = "晚班津貼")
    private BigDecimal nightShiftAllowance;

    @Schema(type = "BigDecimal", description = "全勤津貼")
    private BigDecimal fullAttendanceBonus;

    @Schema(type = "String", description = "身分證字號")
    private String idNumber;

    @Schema(type = "Integer", description = "身分證伙食津貼字號")
    private Integer mealAllowance;

    @Schema(type = "String", description = "員工編號")
    private String employeeNumber;

    @Schema(type = "String", description = "最高學歷")
    private String highestEducationLevel;

    @Schema(type = "String", description = "緊急連絡人通訊地址")
    private String emergencyContactAddress;

    @Schema(type = "String", description = "戶籍地址")
    private String registeredAddress;

    @Schema(type = "String", description = "戶籍勞退自提。0%；1%~6%地址")
    private Float voluntaryPensionContribution;

    @Schema(type = "String", description = "投保眷口數")
    private Integer insuredDependentsCount;

    @Schema(type = "String", description = "代扣稅款")
    private Integer withholdingTax;

    @Schema(type = "String", description = "公司付擔勞保費用")
    private Integer companyLaborInsuranceFee;

    @Schema(type = "String", description = "公司付擔健保費用")
    private Integer companyHealthInsuranceFee;

    @NotNull(message = "所屬公司不能為空")
    @Schema(type = "Byte", description = "所屬公司：0=共通, 1=TG, 2=EG")
    private Byte company;
}
