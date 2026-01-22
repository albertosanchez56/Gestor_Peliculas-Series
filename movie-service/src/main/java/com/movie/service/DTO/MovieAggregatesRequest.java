package com.movie.service.DTO;

import jakarta.validation.constraints.NotNull;

public record MovieAggregatesRequest(
        @NotNull Double averageRating,
        @NotNull Integer voteCount
) {}
