package com.review.service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.review.service.security.JwtAuthFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable())
	        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
	        .httpBasic(b -> b.disable())
	        .formLogin(f -> f.disable())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                "/reviews/movie/**",
	                "/actuator/health",
	                "/v3/api-docs/**",
	                "/swagger-ui/**",
	                "/swagger-ui.html"
	            ).permitAll()
	            .anyRequest().authenticated()
	        );

	    http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
	    return http.build();
	}

}
