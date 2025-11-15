package com.movie.service.Entidades;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "pelicula",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_movie_title_release", columnNames = {"title", "release_date"})
    },
    indexes = {
        @Index(name = "ix_movie_title", columnList = "title"),
        @Index(name = "ix_movie_release_date", columnList = "release_date"),
        @Index(name = "ix_movie_director_id", columnList = "director_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Básicos
    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id", nullable = true)
    private Director director;

    @ManyToMany
    @JoinTable(
        name = "movie_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();
    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CastCredit> cast = new ArrayList<>();


    // Extras recomendados
    private Integer durationMinutes;          // duración

    @Column(length = 10)
    private String originalLanguage;          // ISO 639-1 p.ej. "en", "es"

    @Column(length = 100)
    private String originCountry;             // país principal

    @Column(length = 512)
    private String posterUrl;

    @Column(length = 512)
    private String backdropUrl;

    @Column(length = 512)
    private String trailerUrl;

    @Column(length = 32)
    private String ageRating;                 // "PG-13", "R", "TP", etc.

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MovieStatus status;               // RELEASED, UPCOMING, etc.

    private Long budget;                      // en unidades monetarias
    private Long revenue;

    private Double popularity;                // para ordenar en listados
    private Double averageRating;             // denormalizado del micro de reseñas
    private Integer voteCount;

    @Column(length = 160, unique = true)
    private String slug;                      // "heat-1995"

    // IDs externos
    @Column(length = 32, unique = true)
    private String imdbId;

    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId; // + getters/setters


    // Auditoría
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    /* === Constructores === */
    public Movie() {}

    /* === Getters/Setters === */
    
    
    public Long getId() { return id; }
    public List<CastCredit> getCast() {
		return cast;
	}

	public void setCast(List<CastCredit> cast) {
		this.cast = cast;
	}

	public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public Director getDirector() { return director; }
    public void setDirector(Director director) { this.director = director; }

    public Set<Genre> getGenres() { return genres; }
    public void setGenres(Set<Genre> genres) { this.genres = (genres != null) ? genres : new HashSet<>(); }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getOriginalLanguage() { return originalLanguage; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getBackdropUrl() { return backdropUrl; }
    public void setBackdropUrl(String backdropUrl) { this.backdropUrl = backdropUrl; }

    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

    public MovieStatus getStatus() { return status; }
    public void setStatus(MovieStatus status) { this.status = status; }

    public Long getBudget() { return budget; }
    public void setBudget(Long budget) { this.budget = budget; }

    public Long getRevenue() { return revenue; }
    public void setRevenue(Long revenue) { this.revenue = revenue; }

    public Double getPopularity() { return popularity; }
    public void setPopularity(Double popularity) { this.popularity = popularity; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getVoteCount() { return voteCount; }
    public void setVoteCount(Integer voteCount) { this.voteCount = voteCount; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getImdbId() { return imdbId; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId; }

    public Long getTmdbId() { return tmdbId; }
    public void setTmdbId(Long tmdbId) { this.tmdbId = tmdbId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    /* === Enum de estado === */
    public enum MovieStatus {
        ANNOUNCED, IN_PRODUCTION, POST_PRODUCTION, RELEASED, CANCELED, UPCOMING
    }
}
