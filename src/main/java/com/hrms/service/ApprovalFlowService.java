package com.hrms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.entity.ApprovalFlowConfig;
import com.hrms.entity.Department;
import com.hrms.entity.Employee;
import com.hrms.entity.mapstruct.ApprovalFlowConfigMapper;
import com.hrms.enums.ErrorCode;
import com.hrms.enums.LeaveRecordApprovalStage;
import com.hrms.exception.ServiceException;
import com.hrms.model.bo.ApprovalFlowConfigBO;
import com.hrms.repository.ApprovalFlowConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalFlowService extends ServiceImpl<ApprovalFlowConfigRepository, ApprovalFlowConfig> {
    private final ObjectMapper objectMapper; // 建議全域註冊
    /** key = scopeType:scopeValue (lowercase)，value = FlowPair */
    private final Map<String, FlowPair> cache = new HashMap<>();


    @PostConstruct
    public void initCache(){
        refreshCache();
    }
    /** 供管理端動態刷新 */
    public void refreshCache() {
        cache.clear();
        List<ApprovalFlowConfig> list = baseMapper.selectList(
                new LambdaQueryWrapper<ApprovalFlowConfig>()
                        .eq(ApprovalFlowConfig::getActive, true));
        list.forEach(cfg -> {
            try {
                FlowPair pair = objectMapper.readValue(
                        cfg.getFlowJson(), new TypeReference<FlowPair>() {});
                cache.put(key(cfg.getScopeType(), cfg.getScopeValue()), pair);
            } catch (Exception e) {
                log.error("解析 flow_json 失敗，id={}", cfg.getId(), e);
            }
        });
        log.info("ApprovalFlow cache loaded, size={}", cache.size());
    }
    @Transactional
    public Long upsertFlowConfig(ApprovalFlowConfigBO approvalFlowConfigBO) {
        // ---------- 1) 基本驗證 ----------
        String type = StringUtils.upperCase(approvalFlowConfigBO.getScopeType());
        if (!ArrayUtils.contains(new String[]{"EMPLOYEE","DEPARTMENT","COMPANY","GLOBAL"}, type)) {
            throw new ServiceException(ErrorCode.INVALID_FLOW_TYPE);
        }
        if (StringUtils.isBlank(approvalFlowConfigBO.getScopeValue())) {
            throw new ServiceException(ErrorCode.EMPTY_FLOW_VALUE);
        }

        // ---------- 2) 解析 / 驗證 flowJson ----------
        FlowPair pair;
        try {
            pair = objectMapper.readValue(approvalFlowConfigBO.getFlowJson(), FlowPair.class);
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.INVALID_FLOW_JSON, e);
        }
        if (CollectionUtils.isEmpty(pair.GE_24) || CollectionUtils.isEmpty(pair.LT_24)) {
            throw new ServiceException(ErrorCode.INVALID_FLOW_HOUR);
        }
        // 檢查 stage enum 合法
        Stream.concat(pair.GE_24.stream(), pair.LT_24.stream()).forEach(stage -> {
            if (stage == null) throw new ServiceException(ErrorCode.UNKNOWN_FLOW);
        });

        // ---------- 3) upsert ----------
        LambdaQueryWrapper<ApprovalFlowConfig> qw = new LambdaQueryWrapper<ApprovalFlowConfig>()
                .eq(ApprovalFlowConfig::getScopeType, type)
                .eq(ApprovalFlowConfig::getScopeValue, approvalFlowConfigBO.getScopeValue());

        ApprovalFlowConfig existing = baseMapper.selectOne(qw);
        ApprovalFlowConfig cfg = ApprovalFlowConfigMapper.INSTANCE.toEntity(approvalFlowConfigBO);
//        log.info("upsert flow config, type={}, value={}, cfg={}", type, approvalFlowConfigBO.getScopeValue(), cfg);
        if (existing == null) {
            baseMapper.insert(cfg);
            // 熱更新快取
            cache.put(key(type, approvalFlowConfigBO.getScopeValue()), pair);
            return cfg.getId();
        } else {
            existing.setScopeType(approvalFlowConfigBO.getScopeType());
            existing.setScopeValue(approvalFlowConfigBO.getScopeValue());
            existing.setFlowJson(approvalFlowConfigBO.getFlowJson());
            existing.setActive(approvalFlowConfigBO.getActive());
            baseMapper.updateById(existing);
            cache.put(key(type, approvalFlowConfigBO.getScopeValue()), pair);
            return existing.getId();
        }
    }

    /**
     * 解析並回傳該員工請假單要走的簽核路徑。
     */
    public List<LeaveRecordApprovalStage> resolve(Employee emp, Department dept, boolean ge24) {
        String k;
        // 1) 員工
        k = key("EMPLOYEE", String.valueOf(emp.getId()));
        if (cache.containsKey(k)) return route(cache.get(k), ge24);

        // 2) 部門
        k = key("DEPARTMENT", String.valueOf(dept.getId()));
        if (cache.containsKey(k)) return route(cache.get(k), ge24);

        // 3) 公司
        k = key("COMPANY", emp.getCompany().toString());
        if (cache.containsKey(k)) return route(cache.get(k), ge24);

        // 4) 全域
        k = key("GLOBAL", "*");
        if (cache.containsKey(k)) return route(cache.get(k), ge24);

        return null; // 找不到 → 走預設
    }
    
    public void deleteById(Integer id) {
        ApprovalFlowConfig approvalFlowConfig = baseMapper.selectById(id);
        if ("*".equals(approvalFlowConfig.getScopeValue())) {
            throw new ServiceException(ErrorCode.CAN_NOT_DELETE_PRESET);
        }
        baseMapper.deleteById(id);
    }

    /* ---------- private ---------- */

    private static String key(String type, String val) {
        return type + ":" + val;
    }

    private static List<LeaveRecordApprovalStage> route(FlowPair p, boolean ge24) {
        return ge24 ? p.GE_24 : p.LT_24;
    }
    /** 對應 flow_json 的 POJO */
    public static class FlowPair {
        public List<LeaveRecordApprovalStage> GE_24;
        public List<LeaveRecordApprovalStage> LT_24;
    }
}
