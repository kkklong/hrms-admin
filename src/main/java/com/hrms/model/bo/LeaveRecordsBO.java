package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "LeaveRecordsBO")
@Accessors(chain = true)
public class LeaveRecordsBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "Integer", description = "ID(自動生成)")
    private Integer id;

    @Schema(type = "String", description = "請假原因")
    private String reason;

    @Schema(type = "String", description = "備註")
    private String remark;

    @Schema(type = "Integer", description = "請假紀錄對應的假別id")
    private Integer leaveSpecialRecordsId;

    @Schema(type = "List<LeaveRecordsDateTimeBO>", description = "請假具體日期及班次信息")
    private List<LeaveRecordsDateTimeBO> leaveDates;
}