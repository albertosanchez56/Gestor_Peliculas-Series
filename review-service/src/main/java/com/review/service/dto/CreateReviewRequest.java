package com.review.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReviewRequest {

    @NotNull
    private Long movieId;

    @NotNull
    @Min(1) @Max(10)
    private Integer rating;

    @Size(max = 2000)
    private String comment;

    private boolean containsSpoilers = false;

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public boolean isContainsSpoilers() { return containsSpoilers; }
    public void setContainsSpoilers(boolean containsSpoilers) { this.containsSpoilers = containsSpoilers; }
}
