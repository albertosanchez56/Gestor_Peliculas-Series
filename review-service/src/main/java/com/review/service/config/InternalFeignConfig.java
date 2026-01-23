package com.review.service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalFeignConfig {

    @Bean
    public RequestInterceptor internalTokenInterceptor(@Value("${internal.api-key}") String apiKey) {
        return template -> template.header("X-Internal-Token", apiKey);
    }
}
