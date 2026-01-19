package com.user.service.dto;

public record UserPublicDTO(
        Long id,
        String username,
        String displayName
) {}