package com.hrms.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.Config;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.repository.ConfigRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服務實現類
 * </p>
 *
 * @author System
 * @since 2024-08-26
 */
@Slf4j
@Service
@Transactional
public class ConfigService extends ServiceImpl<ConfigRepository, Config> {

    @Resource
    ConfigRepository configRepository;

    /**
     * 保存配置
     */
    public void saveConfig(Config config) {
        if (config.getId() != null)
            update(config);
        else
            create(config);
    }

    private void create(Config config) {
        // 檢查是否有相同的key
        if (configRepository.existsByConfigKey(config.getConfigKey())) {
            throw new ServiceException(ErrorCode.CONFIG_KEY_ALREADY_EXISTS);
        }
        configRepository.insert(config);
    }

    private void update(Config config) {
        // 檢查是否存在此id
        if (configRepository.selectById(config.getId()) == null) {
            throw new ServiceException(ErrorCode.CONFIG_ID_NOT_FOUND);
        }
        configRepository.updateById(config);
    }

    /**
     * 根據條件動態生成查詢
     */
    public List<Config> searchConfigs(String configKey, String name, Integer sort) {
        return configRepository.searchConfigs(configKey, name, sort);
    }

    public List<Config> getShiftType() {
        return searchConfigs("SHIFT_TYPE", null, null);
    }

    public List<Config> getHoliday() {
        return searchConfigs("HOLIDAY", null, null);
    }
}