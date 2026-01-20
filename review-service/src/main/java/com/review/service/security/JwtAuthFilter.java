package com.review.service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();

        try {
            Claims claims = jwtService.parseClaims(token);

            Long userId = jwtService.extractUserId(claims);
            String username = jwtService.extractUsername(claims);
            String role = jwtService.extractRole(claims); // "ROLE_USER" o "ROLE_ADMIN"

            var auths = (role == null || role.isBlank())
                    ? List.<SimpleGrantedAuthority>of()
                    : List.of(new SimpleGrantedAuthority(role));

            // ✅ principal = userId (para controllers)
            var auth = new UsernamePasswordAuthenticationToken(
                    String.valueOf(userId),
                    null,
                    auths
            );

            // ✅ details = username (por si lo quieres)
            auth.setDetails(username);

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
