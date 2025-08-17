package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "客制簽核流程BO")
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalFlowConfigBO {
    @Schema(description = "PK")
    Integer id;

    @NotNull
    @Schema(description = "EMPLOYEE/DEPARTMENT/COMPANY/GLOBAL")
    String scopeType;

    @NotNull
    @Schema(description = "對應員工ID/部門ID/公司代碼 或 *")
    String scopeValue;

    @NotNull
    @Schema(description = "流程 EX:{\"GE_24\": [\"GM_REVIEW\",\"HR_REVIEW\"], \"LT_24\": [\"HR_REVIEW\"]}")
    String flowJson;

    @NotNull
    @Schema(description = "狀態 1=啟用 0=停用")
    Integer active;
}
