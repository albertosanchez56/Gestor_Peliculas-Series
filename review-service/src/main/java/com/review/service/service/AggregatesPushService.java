package com.review.service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.review.service.Entidades.Review.ReviewStatus;
import com.review.service.client.MovieInternalClient;
import com.review.service.repositorio.ReviewRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AggregatesPushService {

    private static final Logger log = LoggerFactory.getLogger(AggregatesPushService.class);

    private final ReviewRepository reviewRepository;
    private final MovieInternalClient movieInternalClient;

    public AggregatesPushService(ReviewRepository reviewRepository,
                                 MovieInternalClient movieInternalClient) {
        this.reviewRepository = reviewRepository;
        this.movieInternalClient = movieInternalClient;
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "movieInternalClient", fallbackMethod = "pushFallback")
    public void recomputeAndPush(Long movieId) {
        long count = reviewRepository.countByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);

        Double avg = reviewRepository.avgRatingByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);
        if (avg == null) avg = 0.0;

        movieInternalClient.updateAggregates(
            movieId,
            new MovieInternalClient.AggregatesRequest(avg, (int) count)
        );
    }

    /** Fallback cuando movie-service no está disponible: no se envían agregados, se registra y se continúa. */
    @SuppressWarnings("unused")
    private void pushFallback(Long movieId, Throwable ex) {
        log.warn("movie-service no disponible para enviar agregados de película {}: {}", movieId, ex.getMessage());
    }
}

