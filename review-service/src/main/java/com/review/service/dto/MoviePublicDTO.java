package com.review.service.dto;

public record MoviePublicDTO(
        Long id,
        String title,
        String posterUrl,
        Double averageRating
) {}
