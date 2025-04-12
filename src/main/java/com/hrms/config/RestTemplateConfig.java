package com.hrms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableTransactionManagement
public class RestTemplateConfig {

	public static final int TIME_OUT = 30 * 1000;

	@Bean
	public RestTemplate restTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
		rf.setConnectTimeout(TIME_OUT);
		rf.setReadTimeout(TIME_OUT);
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		restTemplate.setInterceptors(interceptors);
		return restTemplate;
	}

}
