package com.hrms.model.bo;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(description = "部門BO")
public class DepartmentBO {


    @Schema(type = "Integer",description = "ID(為自動生成，不用輸入)")
    private Integer id;

    @Schema(type = "Integer",description = "母部門名稱")
    private Integer departmentParent;

    @NotBlank(message = "部門名稱不能為空")
    @Schema(type = "String",description = "部門名稱")
    private String departmentName;

    @Schema(type = "String",description = "描述")
    private String description;

    @NotNull(message = "部門主管不能為空")
    @Schema(type = "Integer",description = "主管員工編號")
    private Integer managerId;

    @NotBlank(message = "預設班別不能為空")
    @Schema(type = "String", description = "預設班別")
    private String workType;

    @Schema(type = "Integer", description = "加班是否換錢，預設是0，目前只有eg的java是1")
    private Integer overtimeType;

    @NotNull(message = "每日早班最少上班人數不能為空")
    @Schema(type = "Integer", description = "每日早班最少上班人數")
    private Integer everyDayMorningCount;

    @NotNull(message = "每日午班最少上班人數不能為空")
    @Schema(type = "Integer", description = "每日午班最少上班人數")
    private Integer everyDayAfternoonCount;

    @NotNull(message = "每日晚班最少上班人數不能為空")
    @Schema(type = "Integer", description = "每日晚班最少上班人數")
    private Integer everyDayNightCount;
}
