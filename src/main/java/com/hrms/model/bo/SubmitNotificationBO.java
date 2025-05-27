package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "通知BO")
public class SubmitNotificationBO {

    @NotNull
    @Schema(type = "Integer", description = "公告通知ID")
    private Integer id;
}
