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

    public String extractUsername(Claims claims) {
        // en tu user-service auth.getName() = username
        // normalmente está en "sub" (subject)
        return claims.getSubject();
    }

    public String extractRole(Claims claims) {
        // depende de cómo lo guardes: "role" o "roles"
        // tú en user-service usas Role USER/ADMIN
        Object role = claims.get("role");
        return role == null ? null : role.toString();
    }

    public Long extractUserId(Claims claims) {
        // ideal: el token lleva userId como claim (ej: "userId")
        Object v = claims.get("userId");
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        return Long.valueOf(v.toString());
    }
}
