package com.movie.service.DTO;

import java.time.LocalDate;

import com.movie.service.Entidades.Movie;

public record MovieSuggestionDTO(
	    Long id,
	    String title,
	    LocalDate releaseDate,
	    String posterUrl
	) {
	    public static MovieSuggestionDTO from(Movie m) {
	        return new MovieSuggestionDTO(m.getId(), m.getTitle(), m.getReleaseDate(), m.getPosterUrl());
	    }
	}

