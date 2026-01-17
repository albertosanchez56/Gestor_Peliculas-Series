package com.review.service.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    public Long userId(Authentication auth) {
        if (auth == null || auth.getDetails() == null) return null;
        return (Long) auth.getDetails();
    }

    public String username(Authentication auth) {
        return auth == null ? null : auth.getName();
    }

    public boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
