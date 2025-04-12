package com.hrms.config;

import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.support.DefaultEventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroEventBusConfig {

    @Bean
    public EventBus eventBus() {
        return new DefaultEventBus();
    }
}