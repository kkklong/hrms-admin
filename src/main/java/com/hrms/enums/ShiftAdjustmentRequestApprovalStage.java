package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 調班流程狀態
 */
@Getter
@AllArgsConstructor
public enum ShiftAdjustmentRequestApprovalStage {
    COMPLETED((byte) 0, "審核完成"),
    LEADER_REVIEW((byte) 1, "組長審核中"),
    HR_REVIEW((byte) 2, "人資審核中"),
    TECH_LEAD_REVIEW((byte) 3, "技術長審核中"),
    GM_REVIEW((byte) 4, "總經理審核中");

    private final Byte value;
    private final String name;

    public static ShiftAdjustmentRequestApprovalStage getApprovalStage(Byte value) {
        for (ShiftAdjustmentRequestApprovalStage approvalStage : ShiftAdjustmentRequestApprovalStage.values()) {
            if (approvalStage.getValue().equals(value)) {
                return approvalStage;
            }
        }
        return null;
    }
}
