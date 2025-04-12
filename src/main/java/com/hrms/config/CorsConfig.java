package com.hrms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 設置跨域請求 Cross Domain
 *
 */
@Configuration
public class CorsConfig extends CorsFilter{

    public CorsConfig() {
        super(corsFilter());
    }

    private static UrlBasedCorsConfigurationSource  corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 設置你要允許的網站域名，如果全允許則設為 *
        config.addAllowedOrigin("*");
        // 如果要限制 HEADER 或 METHOD 請自行更改
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
