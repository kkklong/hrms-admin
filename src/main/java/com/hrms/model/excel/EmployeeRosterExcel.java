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
@Schema(description = "員工名冊Excel")
@Accessors(chain = true)
public class EmployeeRosterExcel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelCell(columnIndex = 0, title = "全名", columnWidth = 10 * 256)
    @Schema(description = "全名")
    private String fullName;

    @ExcelCell(columnIndex = 1, title = "別名", columnWidth = 10 * 256)
    @Schema(description = "別名")
    private String nickName;

    @ExcelCell(columnIndex = 2, title = "性別", columnWidth = 5 * 256)
    @Schema(description = "性別")
    private String gender;

    @ExcelCell(columnIndex = 3, title = "部門名稱", columnWidth = 10 * 256)
    @Schema(description = "部門名稱")
    private String departmentName;

    @ExcelCell(columnIndex = 4, title = "職位", columnWidth = 12 * 256)
    @Schema(description = "職位")
    private String position;

    @ExcelCell(columnIndex = 5, title = "生日", columnWidth = 14 * 256)
    @Schema(description = "生日")
    private LocalDate birthday;

    @ExcelCell(columnIndex = 6, title = "身分證字號", columnWidth = 14 * 256)
    @Schema(description = "身分證字號")
    private String idNumber;

    @ExcelCell(columnIndex = 7, title = "入職時間", columnWidth = 10 * 256, datePattern = DateUtils.DatePattern.YYYY_MM_DD_DASH)
    @Schema(description = "入職時間")
    private LocalDate entryDate;

    @ExcelCell(columnIndex = 8, title = "離職時間", columnWidth = 10 * 256, datePattern = DateUtils.DatePattern.YYYY_MM_DD_DASH)
    @Schema(description = "離職時間")
    private LocalDate outDate;

    @ExcelCell(columnIndex = 9, title = "班制", columnWidth = 5 * 256)
    @Schema(description = "班制")
    private String schedule;

    @ExcelCell(columnIndex = 10, title = "薪資", columnWidth = 12 * 256)
    @Schema(description = "薪資")
    private Long salary;

    @ExcelCell(columnIndex = 11, title = "聯絡電話", columnWidth = 14 * 256)
    @Schema(description = "聯絡電話")
    private String phone;

    @ExcelCell(columnIndex = 12, title = "通訊地址", columnWidth = 40 * 256)
    @Schema(description = "通訊地址")
    private String address;

    @ExcelCell(columnIndex = 13, title = "戶籍地址", columnWidth = 40 * 256)
    @Schema(description = "戶籍地址")
    private String registeredAddress;

    @ExcelCell(columnIndex = 14, title = "最高學歷", columnWidth = 10 * 256)
    @Schema(description = "最高學歷")
    private String highestEducationLevel;

    @ExcelCell(columnIndex = 15, title = "緊急聯絡人", columnWidth = 12 * 256)
    @Schema(description = "緊急聯絡人")
    private String emergencyContact;

    @ExcelCell(columnIndex = 16, title = "關係", columnWidth = 5 * 256)
    @Schema(description = "緊急聯絡人與員工的關係，例如父母、配偶、朋友等")
    private String relationship;

    @ExcelCell(columnIndex = 17, title = "緊急聯絡人通訊地址", columnWidth = 40 * 256)
    @Schema(description = "緊急聯絡人通訊地址")
    private String emergencyContactAddress;

    @ExcelCell(columnIndex = 18, title = "緊急聯絡人電話", columnWidth = 18 * 256)
    @Schema(description = "緊急聯絡人電話")
    private String emergencyContactPhone;

    @Schema(description = "0:離職,1:在職,2:留職,3:其他")
    private Byte status;
}
