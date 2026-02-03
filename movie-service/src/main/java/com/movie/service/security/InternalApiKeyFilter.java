package com.movie.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(InternalApiKeyFilter.class);

    private static final String UNSAFE_DEFAULT = "dev-internal-key";
    private static final String PLACEHOLDER = "CHANGE_ME";

    private final String apiKey;

    public InternalApiKeyFilter(@Value("${internal.api-key:}") String apiKey) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        if (this.apiKey.isEmpty()) {
            throw new IllegalStateException(
                "internal.api-key no está definida. Configura INTERNAL_API_KEY o internal.api-key en tu configuración.");
        }
        if (PLACEHOLDER.equals(this.apiKey) || UNSAFE_DEFAULT.equals(this.apiKey)) {
            log.warn("InternalApiKeyFilter usa un valor por defecto o placeholder ({}). En producción define INTERNAL_API_KEY.", this.apiKey);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.startsWith("/peliculas/internal/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("X-Internal-Token");
        if (header == null || !header.equals(apiKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
