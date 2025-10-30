package com.hrms.common.telegram;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * 服務實現類
 * </p>
 *
 * @author System
 * @since 2024-06-08
 */
@Slf4j
@Service
@Transactional
public class TelegramService {

    @Resource
    private RestTemplate restTemplate;

    private static final String SEND_URL = "http://trisoaring.online:16888/broadcast";
    private static final String API_KEY = "@dafa8888";
    private static final String GROUP_NAME = "查班 Bot 群";

    public void sendMessage(String message) {
        Map<String, String> params  = new HashMap<>();
        params.put("apiKey", API_KEY);
        params.put("groupName", GROUP_NAME);
        params.put("message", message);

        HttpEntity<?> request = new HttpEntity<Object>(params, new HttpHeaders());
        //因只需要印log所以就不做判斷，如果有需要再修改
        ResponseEntity<String> response = restTemplate.exchange(SEND_URL, HttpMethod.POST, request, String.class);
        log.info(response.getBody());
    }

}
