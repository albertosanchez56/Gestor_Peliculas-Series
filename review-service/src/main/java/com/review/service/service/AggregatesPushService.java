package com.review.service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.review.service.Entidades.Review.ReviewStatus;
import com.review.service.client.MovieInternalClient;
import com.review.service.repositorio.ReviewRepository;

@Service
public class AggregatesPushService {

    private final ReviewRepository reviewRepository;
    private final MovieInternalClient movieInternalClient;

    public AggregatesPushService(ReviewRepository reviewRepository,
                                 MovieInternalClient movieInternalClient) {
        this.reviewRepository = reviewRepository;
        this.movieInternalClient = movieInternalClient;
    }

    @Transactional(readOnly = true)
    public void recomputeAndPush(Long movieId) {
        long count = reviewRepository.countByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);

        Double avg = reviewRepository.avgRatingByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);
        if (avg == null) avg = 0.0;

        movieInternalClient.updateAggregates(
            movieId,
            new MovieInternalClient.AggregatesRequest(avg, (int) count)
        );
    }
}

