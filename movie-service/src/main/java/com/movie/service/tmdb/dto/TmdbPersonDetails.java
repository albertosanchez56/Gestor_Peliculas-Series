package com.movie.service.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbPersonDetails(
    long id,
    String biography,
    @JsonProperty("birthday") String birthday,     // "YYYY-MM-DD" o null
    @JsonProperty("deathday") String deathday,     // "YYYY-MM-DD" o null
    @JsonProperty("place_of_birth") String placeOfBirth,
    @JsonProperty("profile_path") String profilePath,
    @JsonProperty("imdb_id") String imdbId
) {}
