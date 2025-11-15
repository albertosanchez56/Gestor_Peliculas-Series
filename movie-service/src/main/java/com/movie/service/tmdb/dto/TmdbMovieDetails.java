// src/main/java/com/movie/service/tmdb/dto/TmdbMovieDetails.java
package com.movie.service.tmdb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovieDetails(
    long id,
    String title,
    String overview,
    @JsonProperty("release_date") String releaseDate,
    Integer runtime,
    @JsonProperty("original_language") String originalLanguage,
    @JsonProperty("poster_path") String posterPath,
    @JsonProperty("backdrop_path") String backdropPath,
    @JsonProperty("vote_average") Double voteAverage,
    @JsonProperty("vote_count") Integer voteCount,

    // NUEVOS
    Long budget,
    Long revenue,
    Double popularity,
    String status, // "Rumored", "Planned", "In Production", "Post Production", "Released", "Canceled"

    @JsonProperty("production_countries")
    List<ProductionCountry> productionCountries,

    List<TmdbGenre> genres
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmdbGenre(long id, String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProductionCountry(
        @JsonProperty("iso_3166_1") String iso_3166_1,
        String name
    ) {}
}
