package com.review.service.Entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_review_movie_user", columnNames = {"movie_id", "user_id"})
    },
    indexes = {
        @Index(name = "ix_review_movie_id", columnList = "movie_id"),
        @Index(name = "ix_review_user_id", columnList = "user_id"),
        @Index(name = "ix_review_movie_created", columnList = "movie_id,created_at")
    }
)
public class Review {

    public enum ReviewStatus {
        VISIBLE,
        HIDDEN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relaciones lógicas (microservicios): IDs
    @NotNull
    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Rating usuario (recomendado 1..10 para que “encaje” con TMDB 0..10)
    @NotNull
    @Min(1)
    @Max(10)
    @Column(nullable = false)
    private Integer rating;

    @Size(max = 2000)
    @Column(length = 2000)
    private String comment;

    @Column(name = "contains_spoilers", nullable = false)
    private boolean containsSpoilers = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ReviewStatus status = ReviewStatus.VISIBLE;

    @Column(nullable = false)
    private boolean edited = false;

    // Auditoría simple (no dependemos de Auditing aún; lo ponemos con @PrePersist)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Review() {}

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // --- getters/setters ---

    public Long getId() { return id; }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public boolean isContainsSpoilers() { return containsSpoilers; }
    public void setContainsSpoilers(boolean containsSpoilers) { this.containsSpoilers = containsSpoilers; }

    public ReviewStatus getStatus() { return status; }
    public void setStatus(ReviewStatus status) { this.status = status; }

    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
