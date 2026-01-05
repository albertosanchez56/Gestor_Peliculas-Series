package com.user.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;


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
          .exceptionHandling(ex -> ex
              .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
          )
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/usuario/auth/register", "/usuario/auth/login").permitAll()
              .requestMatchers("/usuario/auth/me").authenticated()

              // Todo lo demás de /usuario requiere estar autenticado
              // (y luego ya @PreAuthorize decide si ADMIN o no)
              .requestMatchers("/usuario/**").authenticated()

              // El resto abierto (o cámbialo a authenticated si quieres más seguridad)
              .anyRequest().permitAll()
          )
          .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    
    
}
