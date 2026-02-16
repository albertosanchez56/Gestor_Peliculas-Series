package com.movie.service.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.movie.service.tmdb.TmdbClient;
import com.movie.service.tmdb.TmdbClientCircuitBreakerDecorator;
import com.movie.service.tmdb.TmdbClientImpl;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;

/**
 * Configura el <strong>circuit breaker</strong> para las llamadas a la API de TMDB.
 * El bean {@link TmdbClient} que se inyecta en el resto de la aplicación es el
 * decorador que envuelve {@link TmdbClientImpl} con Resilience4j.
 */
@Configuration
@RequiredArgsConstructor
public class TmdbCircuitBreakerConfig {

    /** Nombre del circuit breaker para TMDB (debe coincidir con resilience4j.circuitbreaker.instances.tmdb en config). */
    public static final String TMDB_CIRCUIT_BREAKER_NAME = "tmdb";

    @Bean
    @Primary
    public TmdbClient tmdbClient(TmdbClientImpl delegate, CircuitBreakerRegistry circuitBreakerRegistry) {
        return new TmdbClientCircuitBreakerDecorator(
                delegate,
                circuitBreakerRegistry.circuitBreaker(TMDB_CIRCUIT_BREAKER_NAME)
        );
    }
}
