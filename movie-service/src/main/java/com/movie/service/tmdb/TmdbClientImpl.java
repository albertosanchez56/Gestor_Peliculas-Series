// src/main/java/com/movie/service/tmdb/TmdbClientImpl.java
package com.movie.service.tmdb;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    private UriComponentsBuilder baseWithKey(String base) {
        var b = UriComponentsBuilder.fromHttpUrl(base);
        if (!props.isUseBearerAuth() && props.getApiKey() != null) {
            b.queryParam("api_key", props.getApiKey());
        }
        return b;
    }

    private String withKeyAndLang(String base, String languageOrNull) {
        var b = baseWithKey(base);
        if (languageOrNull != null && !languageOrNull.isBlank()) {
            b.queryParam("language", languageOrNull);
        }
        return b.build().toUriString();
    }

    private String withKeyAndLangCustom(String base, String lang) {
        return baseWithKey(base).queryParam("language", lang).build().toUriString();
    }

    private HttpHeaders bearerHeaders() {
        HttpHeaders h = new HttpHeaders();
        String key = props.getApiKey() != null ? props.getApiKey().trim() : null;
        if (props.isUseBearerAuth() && key != null && !key.isBlank()) {
            h.setBearerAuth(key);
        }
        return h;
    }

    private <T> T get(String uri, Class<T> responseType) {
        if (props.isUseBearerAuth()) {
            HttpEntity<Void> entity = new HttpEntity<>(null, bearerHeaders());
            ResponseEntity<T> re = restTemplate.exchange(uri, HttpMethod.GET, entity, responseType);
            return re.getBody();
        }
        return restTemplate.getForObject(uri, responseType);
    }

    // ==== Detalles ====
    @Override
    public TmdbMovieDetails getMovieDetails(long tmdbId) {
        return getMovieDetails(tmdbId, props.getLanguage());
    }
    @Override
    public TmdbMovieDetails getMovieDetails(long tmdbId, String languageOrNull) {
        String u = withKeyAndLang(url("/movie/" + tmdbId), languageOrNull);
        return get(u, TmdbMovieDetails.class);
    }

    // ==== Créditos ====
    @Override
    public TmdbCredits getMovieCredits(long tmdbId) {
        return getMovieCredits(tmdbId, props.getLanguage());
    }
    @Override
    public TmdbCredits getMovieCredits(long tmdbId, String languageOrNull) {
        String u = withKeyAndLang(url("/movie/" + tmdbId + "/credits"), languageOrNull);
        return get(u, TmdbCredits.class);
    }

    // ==== Vídeos ====
    @Override
    public TmdbVideosResponse getMovieVideos(long tmdbId) {
        // por defecto mantenemos el comportamiento actual
        return getMovieVideos(tmdbId, props.getLanguage(), null);
    }
    @Override
    public TmdbVideosResponse getMovieVideos(long tmdbId, String languageOrNull, String includeVideoLanguageOrNull) {
        var b = baseWithKey(url("/movie/" + tmdbId + "/videos"));
        if (languageOrNull != null && !languageOrNull.isBlank()) {
            b.queryParam("language", languageOrNull);
        }
        if (includeVideoLanguageOrNull != null && !includeVideoLanguageOrNull.isBlank()) {
            b.queryParam("include_video_language", includeVideoLanguageOrNull);
        }
        String u = b.build().toUriString();
        return get(u, TmdbVideosResponse.class);
    }
    @Override
    public TmdbPersonDetails getPersonDetails(long personId) {
        String u = withKeyAndLang(url("/person/" + personId), props.getLanguage());
        return get(u, TmdbPersonDetails.class);
    }

    @Override
    public TmdbVideosResponse getMovieVideos(long tmdbId, String lang) {
        String u = withKeyAndLangCustom(url("/movie/" + tmdbId + "/videos"), lang);
        return get(u, TmdbVideosResponse.class);
    }

    @Override
    public TmdbReleaseDatesResponse getMovieReleaseDates(long tmdbId) {
        String u = baseWithKey(url("/movie/" + tmdbId + "/release_dates")).build().toUriString();
        return get(u, TmdbReleaseDatesResponse.class);
    }

    @Override
    public TmdbPopularResponse getPopular(int page) {
        String u = baseWithKey(url("/movie/popular"))
            .queryParam("language", props.getLanguage())
            .queryParam("page", page)
            .build().toUriString();
        return get(u, TmdbPopularResponse.class);
    }

    @Override
    public TmdbPersonDetails getPersonDetails(long personId, String language) {
        String u = withKeyAndLang(url("/person/" + personId), language);
        return get(u, TmdbPersonDetails.class);
    }
}
