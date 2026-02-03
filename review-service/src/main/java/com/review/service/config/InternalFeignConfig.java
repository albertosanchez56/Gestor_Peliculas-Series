package com.review.service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Configuration
public class InternalFeignConfig {

    private static final String DEV_DEFAULT = "dev-internal-key";
    private static final String PLACEHOLDER = "CHANGE_ME";

    @Bean
    public RequestInterceptor internalTokenInterceptor(
            @Value("${internal.api-key:}") String apiKey,
            Environment env) {
        String resolved = apiKey == null ? "" : apiKey.trim();
        if (resolved.isEmpty()) {
            String[] active = env.getActiveProfiles();
            boolean isDev = active.length == 0 || Arrays.stream(active).anyMatch(p -> "dev".equals(p) || "default".equals(p));
            if (isDev) {
                resolved = DEV_DEFAULT;
            } else {
                throw new IllegalStateException(
                    "internal.api-key no definida. Configura INTERNAL_API_KEY o internal.api-key en tu configuración.");
            }
        }
        if (PLACEHOLDER.equals(resolved) || DEV_DEFAULT.equals(resolved)) {
            org.slf4j.LoggerFactory.getLogger(InternalFeignConfig.class)
                .warn("InternalFeignConfig usa valor por defecto o placeholder ({}). En producción define INTERNAL_API_KEY.", resolved);
        }
        final String token = resolved;
        return template -> template.header("X-Internal-Token", token);
    }
}
