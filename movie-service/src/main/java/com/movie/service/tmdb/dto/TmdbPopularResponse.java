// src/main/java/com/movie/service/tmdb/dto/TmdbPopularResponse.java
package com.movie.service.tmdb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbPopularResponse(
    int page,
    List<Item> results,
    @JsonProperty("total_pages") int totalPages,
    @JsonProperty("total_results") int totalResults
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
        long id,
        String title,
        String overview,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("backdrop_path") String backdropPath,
        @JsonProperty("vote_average") Double voteAverage,
        @JsonProperty("vote_count") Integer voteCount
    ) {}
}
