package com.movie.service.DTO;

import java.time.LocalDate;

public record MoviePublicDTO(
        Long id,
        String title,
        String posterUrl,
        LocalDate releaseDate,
        Long tmdbId,
        Double averageRating,
        Integer voteCount
) {}
