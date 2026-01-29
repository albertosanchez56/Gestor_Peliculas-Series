package com.review.service.dto;

import java.time.Instant;

public record ReviewViewDTO(
    Long id,
    Long movieId,
    Long userId,
    String userDisplayName,
    Integer rating,
    String comment,
    boolean containsSpoilers,
    boolean edited,
    Instant createdAt,
    Instant updatedAt
) {}
