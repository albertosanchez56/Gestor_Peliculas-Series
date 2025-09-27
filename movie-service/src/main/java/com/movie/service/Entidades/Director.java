package com.movie.service.Entidades;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "director",
    indexes = {
        @Index(name = "ix_director_name", columnList = "name"),
        @Index(name = "ix_director_slug", columnList = "slug")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Principal
    @Column(nullable = false, length = 150)
    private String name;

    // Extras recomendados
    @Column(length = 160, unique = true)
    private String slug;                  // p.ej. "christopher-nolan"

    private LocalDate birthDate;          // fecha de nacimiento
    private LocalDate deathDate;          // opcional
    @Column(length = 100)
    private String nationality;           // "US", "UK", "ES" o texto libre

    @Column(length = 512)
    private String photoUrl;              // avatar/foto del director

    @Lob
    @Column(columnDefinition = "TEXT")
    private String biography;             // bio larga

    // IDs externos (por si integras con APIs externas)
    @Column(length = 32, unique = true)
    private String imdbId;

    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "director")
    private List<Movie> movies;

    // Auditoría
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    /* === Constructores === */
    public Director() {}

    /* === Getters/Setters === */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public LocalDate getDeathDate() { return deathDate; }
    public void setDeathDate(LocalDate deathDate) { this.deathDate = deathDate; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }

    public String getImdbId() { return imdbId; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId; }

    public Long getTmdbId() { return tmdbId; }
    public void setTmdbId(Long tmdbId) { this.tmdbId = tmdbId; }

    public List<Movie> getMovies() { return movies; }
    public void setMovies(List<Movie> movies) { this.movies = movies; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
