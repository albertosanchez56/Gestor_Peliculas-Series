package com.review.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class UpdateReviewRequest {

    @Min(1) @Max(10)
    private Integer rating;

    @Size(max = 2000)
    private String comment;

    private Boolean containsSpoilers;

    // opcional: permitir ocultar/mostrar (útil futuro)
    private String status; // "VISIBLE" o "HIDDEN"

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Boolean getContainsSpoilers() { return containsSpoilers; }
    public void setContainsSpoilers(Boolean containsSpoilers) { this.containsSpoilers = containsSpoilers; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
