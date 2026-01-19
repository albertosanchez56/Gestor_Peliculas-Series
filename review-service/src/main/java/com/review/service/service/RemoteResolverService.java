package com.review.service.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import com.review.service.client.MovieClient;
import com.review.service.client.UserClient;
import com.review.service.dto.MoviePublicDTO;
import com.review.service.dto.UserPublicDTO;

@Service
public class RemoteResolverService {

    private final UserClient userClient;
    private final MovieClient movieClient;

    public RemoteResolverService(UserClient userClient, MovieClient movieClient) {
        this.userClient = userClient;
        this.movieClient = movieClient;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "userFallback")
    public UserPublicDTO resolveUser(Long userId) {
        return userClient.getPublicById(userId);
    }

    @CircuitBreaker(name = "movieService", fallbackMethod = "movieFallback")
    public MoviePublicDTO resolveMovie(Long movieId) {
        return movieClient.getMovieById(movieId);
    }

    private UserPublicDTO userFallback(Long userId, Throwable ex) {
        return new UserPublicDTO(userId, "unknown", "Usuario");
    }

    private MoviePublicDTO movieFallback(Long movieId, Throwable ex) {
        return new MoviePublicDTO(movieId, "Película", null, null);
    }
}
