package com.hrms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "查詢部門員工資料Vo")
@Accessors(chain = true)
public class QueryEmployeeVO {

    @Schema(type = "Integer", description = "ID")
    private Integer id;

    @Schema(type = "String", description = "全名")
    private String fullName;

    @Schema(type = "String", description = "別名")
    private String nickName;

    @Schema(type = "String", description = "職位")
    private String position;

    @Schema(type = "Byte", description = "0:離職,1:在職,2:留職,3:其他")
    private Byte status;

    @Schema(type = "BigDecimal", description = "薪資")
    private BigDecimal salary;

    @Schema(type = "String", description = "帳號")
    private String account;

    @Schema(type = "Integer", description = "部⻔ID")
    private Integer departmentId;

    /**
     * 性別
     */
    @Schema(type = "String", description = "性別")
    private String gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 聯絡電話
     */
    private String phone;

    /**
     * 信箱
     */
    private String email;

    /**
     * skype帳號
     */
    private String skype;

    /**
     * telegram帳號
     */
    private String telegram;

    /**
     * ⼊職時間
     */
    private LocalDate entryDate;

    /**
     * 離職時間
     */
    private LocalDate outDate;

    /**
     * 更新密碼時間
     */
    private LocalDateTime PasswordUpdateTime;

    /**
     * 建立者
     */
    private String creatorId;

    /**
     * 建立時間
     */
    private LocalDateTime createDate;

    /**
     * 最後編輯者
     */
    private String updatedId;

    /**
     * 最後更新時間
     */
    private LocalDateTime updateDate;

    /**
     * 緊急聯絡人
     */
    private String emergencyContact;

    /**
     * 通訊地址
     */
    private String address;

    /**
     * 備註說明
     */
    private String remark;

    /**
     * 所在樓層
     */
    private String floor;

    /**
     * 座位編號
     */
    private String seatNumber;

    /**
     * 角色ID
     */
    private Integer roleId;

    @Schema(type = "String", description = "緊急聯絡人與員工的關係，例如父母、配偶、朋友等")
    private String relationship;

    @Schema(type = "String", description = "緊急聯絡人電話")
    private String emergencyContactPhone;

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

    @Schema(type = "String", description = "身份證字號")
    private String idNumber;

    @Schema(type = "Integer", description = "伙食津貼")
    private Integer mealAllowance;

    @Schema(type = "String", description = "員工編號")
    private String employeeNumber;

    @Schema(type = "String", description = "最高學歷")
    private String highestEducationLevel;

    @Schema(type = "String", description = "緊急聯絡人通訊地址")
    private String emergencyContactAddress;

    @Schema(type = "String", description = "戶籍地址")
    private String registeredAddress;

    @Schema(type = "Float", description = "勞退自提比例。0%；1%~6%")
    private Float voluntaryPensionContribution;

    @Schema(type = "Integer", description = "投保眷口數")
    private Integer insuredDependentsCount;

    @Schema(type = "Integer", description = "代扣稅款")
    private Integer withholdingTax;

    @Schema(type = "Integer", description = "公司負擔勞保費用")
    private Integer companyLaborInsuranceFee;

    @Schema(type = "Integer", description = "公司負擔健保費用")
    private Integer companyHealthInsuranceFee;

    @Schema(type = "Byte", description = "所屬公司：0=共通, 1=TG, 2=EG")
    private Byte company;
}
