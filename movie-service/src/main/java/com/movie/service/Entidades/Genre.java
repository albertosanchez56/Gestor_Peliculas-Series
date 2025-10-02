package com.movie.service.Entidades;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "genre",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_genre_name", columnNames = {"name"}),
        @UniqueConstraint(name = "uk_genre_slug", columnNames = {"slug"})
    },
    indexes = {
        @Index(name = "ix_genre_name", columnList = "name"),
        @Index(name = "ix_genre_slug", columnList = "slug")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 120, nullable = false)
    private String slug;                  // p.ej. "accion", "drama"

    @Column(length = 255)
    private String description;

    @Column(length = 7)
    private String colorHex;              // p.ej. "#FF5733"

    @Column(length = 64)
    private String icon;                  // nombre de icono si usas librería

    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies;
    
    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId;

    // Auditoría
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    /* === Constructores === */
    public Genre() {}

    /* === Getters/Setters === */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Set<Movie> getMovies() { return movies; }
    public void setMovies(Set<Movie> movies) { this.movies = movies; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

	public Long getTmdbId() {
		return tmdbId;
	}

	public void setTmdbId(Long tmdbId) {
		this.tmdbId = tmdbId;
	}
    
    
}
