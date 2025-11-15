// src/main/java/com/movie/service/tmdb/dto/TmdbCredits.java
package com.movie.service.tmdb.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbCredits(
	    List<Crew> crew,
	    List<Cast> cast
	) {
	    @JsonIgnoreProperties(ignoreUnknown = true)
	    public record Crew(String job, String name) {}

	    @JsonIgnoreProperties(ignoreUnknown = true)
	    public record Cast(
	        long id,
	        String name,
	        String character,
	        @JsonProperty("profile_path") String profile_path,
	        Integer order,
	        @JsonProperty("known_for_department") String known_for_department,
	        Double popularity
	    ) {}
	}
