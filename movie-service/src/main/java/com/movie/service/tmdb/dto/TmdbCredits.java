// src/main/java/com/movie/service/tmdb/dto/TmdbCredits.java
package com.movie.service.tmdb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbCredits(
    List<Crew> crew
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Crew(String job, String name) {}
}
