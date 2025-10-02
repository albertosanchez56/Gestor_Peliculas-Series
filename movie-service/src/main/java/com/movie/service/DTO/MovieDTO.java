package com.movie.service.DTO;

import java.time.LocalDate;
import java.util.List;

public class MovieDTO {

    // Básicos
    private Long id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private DirectorDTO director;
    private List<GenreDTO> genres;

    // Nuevos (opcionales)
    private Integer durationMinutes;
    private String originalLanguage;
    private String posterUrl;
    private String backdropUrl;
    private String trailerUrl;
    private String ageRating;
    private Double averageRating; // si aún no lo calculas, puedes omitirlo

    public MovieDTO() {}

    // Constructor “completo”
    public MovieDTO(
            Long id,
            String title,
            String description,
            LocalDate releaseDate,
            DirectorDTO director,
            List<GenreDTO> genres,
            Integer durationMinutes,
            String originalLanguage,
            String posterUrl,
            String backdropUrl,
            String trailerUrl,
            String ageRating,
            Double averageRating
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.director = director;
        this.genres = genres;
        this.durationMinutes = durationMinutes;
        this.originalLanguage = originalLanguage;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.trailerUrl = trailerUrl;
        this.ageRating = ageRating;
        this.averageRating = averageRating;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public DirectorDTO getDirector() { return director; }
    public void setDirector(DirectorDTO director) { this.director = director; }

    public List<GenreDTO> getGenres() { return genres; }
    public void setGenres(List<GenreDTO> genres) { this.genres = genres; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getOriginalLanguage() { return originalLanguage; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getBackdropUrl() { return backdropUrl; }
    public void setBackdropUrl(String backdropUrl) { this.backdropUrl = backdropUrl; }

    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
}

