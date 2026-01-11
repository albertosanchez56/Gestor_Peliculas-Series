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

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            // Si no usas form login / basic, mejor apagarlos
            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable())
            .authorizeHttpRequests(auth -> auth
                // Preflight CORS (importante si alguna vez llamas directo a este MS)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Públicos
                .requestMatchers("/usuario/auth/register", "/usuario/auth/login").permitAll()

                // Autenticado (usuario logueado)
                .requestMatchers("/usuario/auth/me").authenticated()
                .requestMatchers("/usuario/ping").authenticated()

                // Admin (siempre bajo /usuario/admin/**)
                .requestMatchers("/usuario/admin/**").hasRole("ADMIN")

                // ✅ DEFAULT: cerrado
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}