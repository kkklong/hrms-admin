package com.hrms.model.excel;

import com.hrms.annotation.ExcelCell;
import com.hrms.util.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;


@Data
@Schema(description = "員工名卡Excel")
@Accessors(chain = true)
public class EmployeeNameCardExcel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "全名")
    private String fullName;

    @Schema(description = "職位")
    private String position;

    @Schema(description = "部門名稱")
    private String departmentName;

    @Schema(description = "性別")
    private String gender;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "聯絡電話")
    private String phone;

    @Schema(description = "薪資")
    private Long salary;

    @ExcelCell(columnIndex = 0, datePattern = DateUtils.DatePattern.YYYY_MM_DD_DASH)
    @Schema(description = "入職時間")
    private LocalDate entryDate;

    @ExcelCell(columnIndex = 0, datePattern = DateUtils.DatePattern.YYYY_MM_DD_DASH)
    @Schema(description = "離職時間")
    private LocalDate outDate;

    @Schema(description = "緊急聯絡人")
    private String emergencyContact;

    @Schema(description = "通訊地址")
    private String address;

    @Schema(description = "備註說明")
    private String remark;

    @Schema(description = "緊急聯絡人與員工的關係，例如父母、配偶、朋友等")
    private String relationship;

    @Schema(description = "緊急聯絡人電話")
    private String emergencyContactPhone;

    @Schema(description = "身分證字號")
    private String idNumber;

    @Schema(description = "最高學歷")
    private String highestEducationLevel;

    @Schema(description = "緊急聯絡人通訊地址")
    private String emergencyContactAddress;

    @Schema(description = "戶籍地址")
    private String registeredAddress;
}
