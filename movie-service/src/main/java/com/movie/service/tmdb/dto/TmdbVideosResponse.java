// src/main/java/com/movie/service/tmdb/dto/TmdbVideosResponse.java
package com.movie.service.tmdb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbVideosResponse(
    List<Video> results
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Video(String site, String type, String key, Boolean official) {}
}

