package com.review.service.dto;

import java.time.Instant;

public class ReviewDTO {
    private Long id;
    private Long movieId;
    private Long userId;
    private Integer rating;
    private String comment;
    private boolean containsSpoilers;
    private String status;
    private boolean edited;
    private Instant createdAt;
    private Instant updatedAt;

    public ReviewDTO() {}

    public ReviewDTO(Long id, Long movieId, Long userId, Integer rating, String comment,
                     boolean containsSpoilers, String status, boolean edited,
                     Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.movieId = movieId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.containsSpoilers = containsSpoilers;
        this.status = status;
        this.edited = edited;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public Long getMovieId() { return movieId; }
    public Long getUserId() { return userId; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public boolean isContainsSpoilers() { return containsSpoilers; }
    public String getStatus() { return status; }
    public boolean isEdited() { return edited; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
