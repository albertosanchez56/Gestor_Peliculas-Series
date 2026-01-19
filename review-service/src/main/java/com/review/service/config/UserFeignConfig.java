package com.review.service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class UserFeignConfig {

    @Bean
    public RequestInterceptor internalTokenInterceptor(@Value("${internal.api-key}") String apiKey) {
        return template -> template.header("X-Internal-Token", apiKey);
    }
}