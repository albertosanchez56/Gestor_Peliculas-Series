package com.movie.service.DTO;

import java.util.List;

public class GenreDTO {

	private Long id;
	private String name;
	private List<MovieDTO> movies;
	
	public GenreDTO() {
		super();
	}
	
	

	public GenreDTO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}



	public GenreDTO(Long id, String name, List<MovieDTO> movies) {
		super();
		this.id = id;
		this.name = name;
		this.movies = movies;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MovieDTO> getMovies() {
		return movies;
	}

	public void setMovies(List<MovieDTO> movies) {
		this.movies = movies;
	}
	
	
	
	
	
}
