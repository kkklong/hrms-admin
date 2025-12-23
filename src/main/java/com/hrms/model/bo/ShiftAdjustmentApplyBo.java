package com.hrms.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "班表調整申請資料")
public class ShiftAdjustmentApplyBo {

    @Schema(description = "變更內容 Map<ShiftId, NewShiftType> (申請人原班表ID, 目標班別)")
    private Map<Integer, String> shiftMap;

    @Schema(description = "申請原因")
    private String reason;
}