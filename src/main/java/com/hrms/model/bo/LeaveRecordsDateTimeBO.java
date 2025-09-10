package com.hrms.model.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "LeaveRecordsDateTimeBO")
@Accessors(chain = true)
public class LeaveRecordsDateTimeBO implements Serializable {

    @NotNull
    @Schema(type = "LocalDateTime", description = "請假開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime startDate;

    @NotNull
    @Schema(type = "LocalDateTime", description = "請假結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime endDate;

    @NotNull
    @Schema(type = "Float", description = "小時計算，可以有小數點")
    private  Float countVal;

    @NotBlank(message = "班次類型不能為空")
    @Schema(type = "String", description = "班次類型，如早班、午班、大夜班")
    private String shiftType;

    @NotNull
    @Schema(description = "包含休息1小時(不包含:0、包含:1) ，如是1的話，請假時數要扣1小時")
    private Boolean includesBreak;
}
