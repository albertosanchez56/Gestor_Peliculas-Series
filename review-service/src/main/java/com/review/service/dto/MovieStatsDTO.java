package com.review.service.dto;

public class MovieStatsDTO {
    private Double averageUserRating;
    private Long voteCount;

    public MovieStatsDTO() {}

    public MovieStatsDTO(Double averageUserRating, Long voteCount) {
        this.averageUserRating = averageUserRating;
        this.voteCount = voteCount;
    }

    public Double getAverageUserRating() { return averageUserRating; }
    public Long getVoteCount() { return voteCount; }
}

