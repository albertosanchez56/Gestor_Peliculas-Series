package com.review.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.review.service.config.InternalFeignConfig;
import com.review.service.dto.MovieAggregatesRequest;
import com.review.service.dto.MoviePublicDTO;

@FeignClient(
    name = "movie-service",
    contextId = "movieClient",
    configuration = InternalFeignConfig.class
)
public interface MovieClient {

    @GetMapping("/peliculas/peliculas/{id}")
    MoviePublicDTO getMovieById(@PathVariable("id") Long id);

    @PatchMapping(
        value = "/peliculas/internal/movies/{id}/aggregates",
        consumes = "application/json"
    )
    void updateAggregates(@PathVariable("id") Long id,
                          @RequestBody MovieAggregatesRequest body);
}
