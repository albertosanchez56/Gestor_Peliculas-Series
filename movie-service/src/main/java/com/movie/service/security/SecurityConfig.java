package com.movie.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final InternalApiKeyFilter internalApiKeyFilter;

    public SecurityConfig(InternalApiKeyFilter internalApiKeyFilter) {
        this.internalApiKeyFilter = internalApiKeyFilter;
    }

    /** Cadena solo para /tmdb/**: sin InternalApiKeyFilter, permitAll. Evita 403 desde el front vía Gateway. */
    @Bean
    @Order(1)
    SecurityFilterChain tmdbFilterChain(HttpSecurity http) throws Exception {
        return http
            // Matcher por URI por si el path llega con otro formato (proxy, etc.)
            .securityMatcher(request -> {
                String uri = request.getRequestURI();
                return uri != null && (uri.startsWith("/tmdb") || uri.contains("/tmdb/"));
            })
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable())
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            	    // internos (permitAll pero protegidos por filtro)
            	    .requestMatchers("/peliculas/internal/**").permitAll()

            	    // públicos
            	    .requestMatchers("/peliculas/**").permitAll()
            	    .requestMatchers("/directores/**").permitAll()
            	    .requestMatchers("/generos/**").permitAll()

            	    .anyRequest().denyAll()
            	);

        // filtro interno antes de todo (no aplica a /tmdb/** por la cadena anterior)
        http.addFilterBefore(internalApiKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
