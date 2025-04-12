package com.hrms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.common.ApiResponse;
import com.hrms.enums.ErrorCode;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class DefaultShiroFilter extends BasicHttpAuthenticationFilter {
    @Resource
    ObjectMapper objectMapper;

    @Autowired
    public DefaultShiroFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getRemoteAddr();
        String requestUri = httpRequest.getRequestURI();
        log.warn("Unauthorized access attempt from IP: {}, API: {}", clientIp, requestUri);

        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.setContentType("application/json");

        ApiResponse<Integer> stringApiResponse = new ApiResponse<>(ErrorCode.UNAUTHORIZED);

        httpResponse.getWriter().write(objectMapper.writeValueAsString(stringApiResponse));
        return false;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return super.isAccessAllowed(request, response, mappedValue);
    }
}
