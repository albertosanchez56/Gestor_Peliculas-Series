package com.user.service.dto;

import com.user.service.Entidades.Role;
import com.user.service.Entidades.Status;
import java.time.Instant;

public record UserAdminDTO(
        Long id,
        String email,
        String username,
        String displayName,
        Role role,
        Status status,
        Instant createdAt,
        Instant updatedAt
) {}
