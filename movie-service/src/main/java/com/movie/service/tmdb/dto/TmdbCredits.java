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
	    public record Crew(Long id, String job, String name,
                @JsonProperty("department") String department,
                @JsonProperty("profile_path") String profilePath) {}

	    @JsonIgnoreProperties(ignoreUnknown = true)
	    public record Cast(
	    	    Long id,
	    	    @JsonProperty("known_for_department") String known_for_department,
	    	    String name,
	    	    String character,
	    	    Integer order,
	    	    Double popularity,
	    	    @JsonProperty("profile_path") String profilePath
	    	) {}
	}
