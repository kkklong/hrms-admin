package com.hrms.entity.mapstruct;


import com.hrms.entity.ApprovalFlowConfig;
import com.hrms.enums.CompanyType;
import com.hrms.enums.LeaveRecordApprovalStage;
import com.hrms.enums.ReviewInterval;
import com.hrms.enums.ScopeType;
import com.hrms.model.bo.ApprovalFlowConfigBO;
import com.hrms.model.vo.CompanyVO;
import com.hrms.model.vo.LeaveRecordApprovalStageVO;
import com.hrms.model.vo.ReviewIntervalVO;
import com.hrms.model.vo.ScopeTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ApprovalFlowConfigMapper {

    ApprovalFlowConfigMapper INSTANCE = Mappers.getMapper(ApprovalFlowConfigMapper.class);

    ApprovalFlowConfig toEntity(ApprovalFlowConfigBO bo);

    // Enum 轉 VO，過濾 COMPLETED，去掉"中"
    default List<LeaveRecordApprovalStageVO> enumToLeaveRecordApprovalStageVOs() {
        return Arrays.stream(LeaveRecordApprovalStage.values())
                .filter(e -> e != LeaveRecordApprovalStage.COMPLETED)
                .map(e -> new LeaveRecordApprovalStageVO(
                        e.getName() != null ? e.getName().replace("中", "") : null,
                        e.name()
                ))
                .collect(Collectors.toList());
    }

    default List<ReviewIntervalVO> enumToReviewIntervalVOs() {
        return Arrays.stream(ReviewInterval.values())
                .map(e -> new ReviewIntervalVO(
                        e.getName(),
                        e.name()
                ))
                .collect(Collectors.toList());
    }

    default List<ScopeTypeVO> enumToScopeTypeVOs() {
        return Arrays.stream(ScopeType.values())
                .map(e -> new ScopeTypeVO(
                        e.getName(),
                        e.name()
                ))
                .collect(Collectors.toList());
    }

    // Enum 轉 VO，過濾 COMMON
    default List<CompanyVO> enumToCompanyVOs() {
        return Arrays.stream(CompanyType.values())
                .filter(e -> e != CompanyType.COMMON)
                .map(e -> new CompanyVO(
                        e.getName(),
                        String.valueOf(e.getValue())
                ))
                .collect(Collectors.toList());
    }
}
