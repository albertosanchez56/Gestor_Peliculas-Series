package com.review.service.dto;

import java.time.Instant;

public record ReviewViewDTO(
	    Long id,
	    Long movieId,
	    Long userId,
	    String displayName,
	    Integer rating,
	    String comment,
	    boolean containsSpoilers,
	    boolean edited,
	    Instant createdAt,
	    Instant updatedAt
	) {}
