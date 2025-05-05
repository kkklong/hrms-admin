package com.hrms.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "部門Vo")
@Accessors(chain = true)
public class DepartmentVO implements Serializable {

    @Schema(type = "Integer", description = "部門ID")
    private Integer id;

    @Schema(type = "Integer", description = "母部門名稱")
    private Integer departmentParent;

    @Schema(type = "String", description = "部門名稱")
    private String departmentName;

    @Schema(type = "String", description = "描述")
    private String description;

    @Schema(type = "Integer", description = "主管員工編號")
    private Integer managerId;

    @Schema(type = "String", description = "主管別名")
    private String  managerNickName;

    @Schema(type = "string", format = "date-time", description = "創建時間")
    private LocalDateTime createdDate;

    @Schema(type = "string", format = "date-time", description = "更新時間")
    private LocalDateTime updatedDate;

    @Schema(type = "String", description = "預設班別")
    private String workType;

    @Schema(type = "Integer", description = "加班是否換錢，預設是0，目前只有eg的java是1")
    private Integer overtimeType;

    @Schema(type = "Integer", description = "每日早班最少上班人數")
    private Integer everyDayMorningCount;

    @Schema(type = "Integer", description = "每日午班最少上班人數")
    private Integer everyDayAfternoonCount;

    @Schema(type = "Integer", description = "每日晚班最少上班人數")
    private Integer everyDayNightCount;

    @Schema(type = "Byte", description = "所屬公司：0=共通, 1=TG, 2=EG")
    private Byte company;
}
