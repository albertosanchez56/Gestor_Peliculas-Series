package com.review.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.review.service.config.InternalFeignConfig;


@FeignClient(
	    name = "movie-service",
	    contextId = "movieInternalClient",
	    configuration = InternalFeignConfig.class
	)
	public interface MovieInternalClient {

    @PatchMapping(value = "/peliculas/internal/movies/{id}/aggregates")
    void updateAggregates(@PathVariable("id") Long movieId,
                          @RequestBody AggregatesRequest body);

    record AggregatesRequest(Double averageRating, Integer voteCount) {}
}
