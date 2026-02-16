package com.gateway.service3.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

/**
 * Circuit breaker para llamadas del gateway a movie-service (películas, directores, géneros).
 */
@Configuration
public class CircuitBreakerConfig {

    public static final String PELICULAS_CIRCUIT_BREAKER = "peliculas";

    @Bean
    public CircuitBreaker peliculasCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker(PELICULAS_CIRCUIT_BREAKER);
    }
}
