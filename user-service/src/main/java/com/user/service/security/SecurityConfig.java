package com.user.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final InternalApiKeyFilter internalApiKeyFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, InternalApiKeyFilter internalApiKeyFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.internalApiKeyFilter = internalApiKeyFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Públicos
                .requestMatchers("/usuario/auth/register", "/usuario/auth/login").permitAll()

                // ✅ Internos (permitAll, pero el filtro exige X-Internal-Token)
                .requestMatchers("/usuario/internal/**").permitAll()

                // Autenticado
                .requestMatchers("/usuario/auth/me/**").authenticated()
                .requestMatchers("/usuario/ping").authenticated()

                // Admin
                .requestMatchers("/usuario/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            );

        // ✅ primero el filtro interno
        http.addFilterBefore(internalApiKeyFilter, UsernamePasswordAuthenticationFilter.class);
        // ✅ luego el JWT
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}