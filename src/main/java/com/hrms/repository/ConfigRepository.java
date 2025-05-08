package com.hrms.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hrms.entity.Config;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author System
 * @since 2024-08-26
 */
public interface ConfigRepository extends BaseMapper<Config> {

    /**
     * 根據條件動態生成查詢
     */
    default List<Config> searchConfigs(String configKey, String name, Integer sort) {
        QueryWrapper<Config> queryWrapper = Wrappers.query();
        if (configKey != null && !configKey.isEmpty()) {
            queryWrapper.like("config_key", configKey);
        }
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (sort != null) {
            queryWrapper.eq("sort", sort);
        }
        return selectList(queryWrapper);
    }

    /**
     * 判断 configKey 是否存在
     */
    default boolean existsByConfigKey(String configKey) {
        QueryWrapper<Config> queryWrapper = Wrappers.query();
        queryWrapper.eq("config_key", configKey);
        return selectCount(queryWrapper) > 0;
    }

}
