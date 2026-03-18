package com.gateway.service3.configuracion;

import org.springframework.context.annotation.Configuration;

/**
 * CORS se configura en `application.yaml` mediante `spring.cloud.gateway.globalcors`.
 * Mantenerlo en un único sitio evita duplicidades y comportamientos confusos.
 */
@Configuration
public class CorsGlobalConfiguration {
}
