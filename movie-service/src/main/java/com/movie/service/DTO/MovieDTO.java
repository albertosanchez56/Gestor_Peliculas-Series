package com.movie.service.DTO;

import java.time.LocalDate;
import java.util.List;

public class MovieDTO {

	private Long id;
	private String title;
	private String description;
	private LocalDate releaseDate;
	private DirectorDTO director;
	private List<GenreDTO> genres;
	
	public MovieDTO() {
		super();
	}

	public MovieDTO(Long id, String title, String description, LocalDate releaseDate, DirectorDTO director,
			List<GenreDTO> genres) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.releaseDate = releaseDate;
		this.director = director;
		this.genres = genres;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	public DirectorDTO getDirector() {
		return director;
	}

	public void setDirector(DirectorDTO director) {
		this.director = director;
	}

	public List<GenreDTO> getGenres() {
		return genres;
	}

	public void setGenres(List<GenreDTO> genres) {
		this.genres = genres;
	}
	
	
	
	
}
