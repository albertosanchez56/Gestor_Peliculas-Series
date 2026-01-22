package com.review.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.review.service.dto.MoviePublicDTO;

@FeignClient(name = "movie-service", contextId = "movieClient")
public interface MovieClient {

    @GetMapping("/peliculas/peliculas/{id}")
    MoviePublicDTO getMovieById(@PathVariable("id") Long id);
}

