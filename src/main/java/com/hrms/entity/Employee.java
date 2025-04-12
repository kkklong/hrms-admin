package com.hrms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author System
 * @since 2024-11-22
 */
@Data
public class Employee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 職員ID(自動生成)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 全名
     */
    private String fullName;

    /**
     * 別名
     */
    private String nickName;

    /**
     * 職位
     */
    private String position;

    /**
     * 0:離職,1:在職,2:留職,3:其他
     */
    private Byte status;

    /**
     * 薪資
     */
    private Long salary;

    /**
     * 帳號
     */
    private String account;

    /**
     * 密碼
     */
    private String password;

    /**
     * 部門ID
     */
    private Integer departmentId;

    /**
     * 性別
     */
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
     * TELEGRAM帳號
     */
    private String telegram;

    /**
     * 入職時間
     */
    private LocalDate entryDate;

    /**
     * 離職時間
     */
    private LocalDate outDate;

    /**
     * 更新密碼時間
     */
    private LocalDateTime passwordUpdateTime;

    /**
     * 創建時間
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;

    /**
     * 更新時間
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedDate;

    /**
     * employee.account
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdId;

    /**
     * employee.account
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedId;

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

    /**
     * 緊急聯絡人與員工的關係，例如父母、配偶、朋友等
     */
    private String relationship;

    /**
     * 緊急聯絡人電話
     */
    private String emergencyContactPhone;

    /**
     * 加班是否換錢，預設是0，目前只有eg的java是1
     */
    private Integer overtimeType;

    /**
     * 勞保費用
     */
    private Integer laborInsuranceFee;

    /**
     * 健保費用
     */
    private Integer healthInsuranceFee;

    /**
     * 假日值班津貼
     */
    private Integer holidayDutyAllowance;

    /**
     * 午班津貼
     */
    private Integer afternoonShiftAllowance;

    /**
     * 晚班津貼
     */
    private Integer nightShiftAllowance;

    /**
     * 全勤獎金
     */
    private Integer fullAttendanceBonus;

    /**
     * 身分證字號
     */
    private String idNumber;

    /**
     * 伙食津貼
     */
    private Integer mealAllowance;

    /**
     * 員工編號
     */
    private String employeeNumber;

    /**
     * 最高學歷
     */
    private String highestEducationLevel;

    /**
     * 緊急聯絡人通訊地址
     */
    private String emergencyContactAddress;

    /**
     * 戶籍地址
     */
    private String registeredAddress;

    /**
     * 勞退自提。0%；1%~6%
     */
    private Float voluntaryPensionContribution;

    /**
     * 投保眷口數
     */
    private Integer insuredDependentsCount;

    /**
     * 代扣稅款
     */
    private Integer withholdingTax;

    /**
     * 公司負擔勞保費用
     */
    private Integer companyLaborInsuranceFee;

    /**
     * 公司負擔健保費用
     */
    private Integer companyHealthInsuranceFee;

    /**
     * 所屬公司：0=共通, 1=TG, 2=EG
     */
    private Byte company;
}
