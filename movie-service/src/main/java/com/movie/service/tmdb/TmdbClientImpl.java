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

    private String url(String path) { return props.getBaseUrl() + path; }

    private String withKey(String base) {
        return UriComponentsBuilder.fromHttpUrl(base)
            .queryParam("api_key", props.getApiKey())
            .build().toUriString();
    }

    private String withKeyAndLang(String base, String languageOrNull) {
        var b = UriComponentsBuilder.fromHttpUrl(base)
            .queryParam("api_key", props.getApiKey());
        if (languageOrNull != null && !languageOrNull.isBlank()) {
            b.queryParam("language", languageOrNull);
        }
        return b.build().toUriString();
    }
    
    private String withKeyAndLangCustom(String base, String lang) {
        return UriComponentsBuilder.fromHttpUrl(base)
            .queryParam("api_key", props.getApiKey())
            .queryParam("language", lang)
            .build()
            .toUriString();
    }

    // ==== Detalles ====
    @Override
    public TmdbMovieDetails getMovieDetails(long tmdbId) {
        return getMovieDetails(tmdbId, props.getLanguage());
    }
    @Override
    public TmdbMovieDetails getMovieDetails(long tmdbId, String languageOrNull) {
        String u = withKeyAndLang(url("/movie/" + tmdbId), languageOrNull);
        return restTemplate.getForObject(u, TmdbMovieDetails.class);
    }

    // ==== Créditos ====
    @Override
    public TmdbCredits getMovieCredits(long tmdbId) {
        return getMovieCredits(tmdbId, props.getLanguage());
    }
    @Override
    public TmdbCredits getMovieCredits(long tmdbId, String languageOrNull) {
        String u = withKeyAndLang(url("/movie/" + tmdbId + "/credits"), languageOrNull);
        return restTemplate.getForObject(u, TmdbCredits.class);
    }

    // ==== Vídeos ====
    @Override
    public TmdbVideosResponse getMovieVideos(long tmdbId) {
        // por defecto mantenemos el comportamiento actual
        return getMovieVideos(tmdbId, props.getLanguage(), null);
    }
    @Override
    public TmdbVideosResponse getMovieVideos(long tmdbId, String languageOrNull, String includeVideoLanguageOrNull) {
        var b = UriComponentsBuilder.fromHttpUrl(url("/movie/" + tmdbId + "/videos"))
            .queryParam("api_key", props.getApiKey());
        if (languageOrNull != null && !languageOrNull.isBlank()) {
            b.queryParam("language", languageOrNull);
        }
        if (includeVideoLanguageOrNull != null && !includeVideoLanguageOrNull.isBlank()) {
            // Ej: "es,en,null" para incluir vídeos en varios idiomas
            b.queryParam("include_video_language", includeVideoLanguageOrNull);
        }
        String u = b.build().toUriString();
        return restTemplate.getForObject(u, TmdbVideosResponse.class);
    }
    @Override
    public TmdbPersonDetails getPersonDetails(long personId) {
        String u = withKeyAndLang(url("/person/" + personId), props.getLanguage());
        return restTemplate.getForObject(u, TmdbPersonDetails.class);
    }
    
    @Override
    public TmdbVideosResponse getMovieVideos(long tmdbId, String lang) {
        String u = withKeyAndLangCustom(url("/movie/" + tmdbId + "/videos"), lang);
        return restTemplate.getForObject(u, TmdbVideosResponse.class);
    }

    // ==== Release dates y Popular sin cambios ====
    @Override
    public TmdbReleaseDatesResponse getMovieReleaseDates(long tmdbId) {
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
    
    @Override
    public TmdbPersonDetails getPersonDetails(long personId, String language) {
        String u = withKeyAndLang(url("/person/" + personId), language);
        return restTemplate.getForObject(u, TmdbPersonDetails.class);
    }
}
