// src/main/java/com/movie/service/tmdb/TmdbClientImpl.java
package com.movie.service.tmdb;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.movie.service.configuracion.TmdbProps;
import com.movie.service.tmdb.dto.*;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TmdbClientImpl implements TmdbClient {

    private final TmdbProps props;
    private final RestTemplate restTemplate = new RestTemplate();

    private String url(String path) {
        return props.getBaseUrl() + path;
    }

    private String withKeyAndLang(String base) {
        return UriComponentsBuilder.fromHttpUrl(base)
                .queryParam("api_key", props.getApiKey())
                .queryParam("language", props.getLanguage())
                .build()
                .toUriString();
    }

    @Override
    public TmdbMovieDetails getMovieDetails(long tmdbId) {
        String u = withKeyAndLang(url("/movie/" + tmdbId));
        return restTemplate.getForObject(u, TmdbMovieDetails.class);
    }

    @Override
    public TmdbCredits getMovieCredits(long tmdbId) {
        String u = withKeyAndLang(url("/movie/" + tmdbId + "/credits"));
        return restTemplate.getForObject(u, TmdbCredits.class);
    }

    @Override
    public TmdbVideosResponse getMovieVideos(long tmdbId) {
        String u = withKeyAndLang(url("/movie/" + tmdbId + "/videos"));
        return restTemplate.getForObject(u, TmdbVideosResponse.class);
    }

    @Override
    public TmdbReleaseDatesResponse getMovieReleaseDates(long tmdbId) {
        // este endpoint no usa "language"
        String u = UriComponentsBuilder.fromHttpUrl(url("/movie/" + tmdbId + "/release_dates"))
                .queryParam("api_key", props.getApiKey())
                .build().toUriString();
        return restTemplate.getForObject(u, TmdbReleaseDatesResponse.class);
    }

    @Override
    public TmdbPopularResponse getPopular(int page) {
        String u = UriComponentsBuilder.fromHttpUrl(url("/movie/popular"))
                .queryParam("api_key", props.getApiKey())
                .queryParam("language", props.getLanguage())
                .queryParam("page", page)
                .build().toUriString();
        return restTemplate.getForObject(u, TmdbPopularResponse.class);
    }
}
