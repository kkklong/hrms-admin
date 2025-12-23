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
import com.hrms.enums.ScopeType;
import com.hrms.exception.ServiceException;
import com.hrms.model.bo.ApprovalFlowConfigBO;
import com.hrms.repository.ApprovalFlowConfigRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 客製簽核流程設定表 服務實現類
 * </p>
 *
 * @author System
 * @since 2025-11-06
 */
@Slf4j
@Service
@Transactional
public class ApprovalFlowConfigService extends ServiceImpl<ApprovalFlowConfigRepository, ApprovalFlowConfig> {

    @Resource
    private  ObjectMapper objectMapper; // 建議全域註冊
    /** key = scopeType:scopeValue (lowercase)，value = FlowPair */
    private  Map<String, FlowPair> cache = new HashMap<>();

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
    public void upsertFlowConfig(@Valid  ApprovalFlowConfigBO approvalFlowConfigBO) {
        // ---------- 1) 解析 / 驗證 flowJson ----------
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

        // ---------- 2) upsert ----------
        String type = approvalFlowConfigBO.getScopeType();

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
        } else {
            existing.setScopeType(approvalFlowConfigBO.getScopeType());
            existing.setScopeValue(approvalFlowConfigBO.getScopeValue());
            existing.setFlowJson(approvalFlowConfigBO.getFlowJson());
            // 全局(Global) 規則在更新時不可被停用：強制維持啟用
            if (ScopeType.GLOBAL.name().equalsIgnoreCase(existing.getScopeType())) {
                existing.setActive(1);
            } else {
                existing.setActive(approvalFlowConfigBO.getActive());
            }
            baseMapper.updateById(existing);
            cache.put(key(type, approvalFlowConfigBO.getScopeValue()), pair);
        }
    }

    /**
     * 供管理端動態刷新
     */
    @Transactional
    public boolean createFlowConfig(@Valid ApprovalFlowConfigBO approvalFlowConfigBO) {
        // ---------- 1) 解析 / 驗證 flowJson ----------
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

        // ---------- 2) 僅新增，不做更新 ----------
        String type = approvalFlowConfigBO.getScopeType();
        LambdaQueryWrapper<ApprovalFlowConfig> qw = new LambdaQueryWrapper<ApprovalFlowConfig>()
                .eq(ApprovalFlowConfig::getScopeType, type)
                .eq(ApprovalFlowConfig::getScopeValue, approvalFlowConfigBO.getScopeValue());

        ApprovalFlowConfig existing = baseMapper.selectOne(qw);
        if (existing != null) {
            // 已存在對應規則：不異動資料庫，返回錯誤訊息
            throw new ServiceException(ErrorCode.CONFIG_KEY_ALREADY_EXISTS);
        }

        ApprovalFlowConfig cfg = ApprovalFlowConfigMapper.INSTANCE.toEntity(approvalFlowConfigBO);
        baseMapper.insert(cfg);
        // 熱更新快取
        cache.put(key(type, approvalFlowConfigBO.getScopeValue()), pair);
        return true;
    }

    /**
     * 解析並回傳該員工請假單要走的簽核路徑。
     */
    public <T extends Enum<T>> List<T> resolve(Employee emp, Department dept, boolean ge24, Class<T> enumClass) {
        String k;

        // 1) 員工
        k = key("EMPLOYEE", String.valueOf(emp.getId()));
        if (cache.containsKey(k)) return getAndConvert(k, ge24, enumClass);

        // 2) 部門
        k = key("DEPARTMENT", String.valueOf(dept.getId()));
        if (cache.containsKey(k)) return getAndConvert(k, ge24, enumClass);

        // 3) 公司
        k = key("COMPANY", emp.getCompany().toString());
        if (cache.containsKey(k)) return getAndConvert(k, ge24, enumClass);

        // 4) 全域
        k = key("GLOBAL", "*");
        if (cache.containsKey(k)) return getAndConvert(k, ge24, enumClass);

        return null;
    }

    private <T extends Enum<T>> List<T> getAndConvert(String key, boolean ge24, Class<T> enumClass) {

        FlowPair<String> pair = (FlowPair<String>) cache.get(key);

        List<String> targetList = ge24 ? pair.GE_24 : pair.LT_24;

        if (targetList == null || targetList.isEmpty()) {
            return Collections.emptyList();
        }
        return targetList.stream()
                .map(name -> Enum.valueOf(enumClass, name))
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        ApprovalFlowConfig approvalFlowConfig = baseMapper.selectById(id);
        if (ScopeType.GLOBAL.name().equals(approvalFlowConfig.getScopeType())) {
            throw new ServiceException(ErrorCode.CAN_NOT_DELETE_PRESET);
        }
        baseMapper.deleteById(id);
        refreshCache();
    }

    /* ---------- private ---------- */

    private static String key(String type, String val) {
        return type + ":" + val;
    }

    private <T> List<T> route(FlowPair<T> p, boolean ge24) {
        return ge24 ? p.GE_24 : p.LT_24;
    }
    /** 對應 flow_json 的 POJO */
    public static class FlowPair<T> {
        public List<T> GE_24;
        public List<T> LT_24;
    }



}
