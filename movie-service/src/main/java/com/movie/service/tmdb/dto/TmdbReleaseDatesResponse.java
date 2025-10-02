// src/main/java/com/movie/service/tmdb/dto/TmdbReleaseDatesResponse.java
package com.movie.service.tmdb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbReleaseDatesResponse(
    List<Result> results
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
        @JsonProperty("iso_3166_1") String iso_3166_1,
        @JsonProperty("release_dates") List<ReleaseDate> release_dates
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ReleaseDate(String certification) {}
}
