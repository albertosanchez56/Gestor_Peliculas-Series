package com.review.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //  userId real: en tu user-service el subject ES el userId
    public String extractUsername(Claims claims) {
        return claims.get("username", String.class);
    }

    public Long extractUserId(Claims claims) {
        // subject = userId
        return Long.valueOf(claims.getSubject());
    }

    public String extractRole(Claims claims) {
        // aquí te viene "ROLE_USER" ya prefijado
        return claims.get("role", String.class);
    }

}
