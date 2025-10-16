package com.hrms.config;

import com.hrms.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RedisConfigTest {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final Map<String, String> jsonMap = new HashMap<>();


    @Test
    public void testRedisConnection() {
        String key = "testKey";
        String value = "testValue";

        // 存入數據
        redisTemplate.opsForValue().set(key, value);

        // 獲取數據
        Object result = redisTemplate.opsForValue().get(key);

        // 驗證結果
        assertEquals(value, result);

        // 刪除數據
        redisTemplate.delete(key);

        // 檢查數據是否已被刪除
        boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));

        // 驗證數據是否已被刪除
        assertFalse(hasKey);
    }

    void check(String key, Class clazz) {
        redisTemplate.delete(key);
        Object data = clazz.equals(String.class) ? jsonMap.get(key) : JsonUtils.toObject(jsonMap.get(key), clazz);
        assertNotNull(data);
        redisTemplate.opsForValue().set(key, data, Duration.ofHours(2));
        Object dataFromRedis = redisTemplate.opsForValue().get(key);
        assertNotNull(dataFromRedis);
        redisTemplate.delete(key);
    }
}