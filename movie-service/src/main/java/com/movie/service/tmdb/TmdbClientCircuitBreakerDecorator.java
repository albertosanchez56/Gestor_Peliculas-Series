package com.movie.service.tmdb;

import java.util.function.Supplier;

import org.springframework.lang.Nullable;

import com.movie.service.tmdb.dto.TmdbCredits;
import com.movie.service.tmdb.dto.TmdbMovieDetails;
import com.movie.service.tmdb.dto.TmdbPersonDetails;
import com.movie.service.tmdb.dto.TmdbPopularResponse;
import com.movie.service.tmdb.dto.TmdbReleaseDatesResponse;
import com.movie.service.tmdb.dto.TmdbVideosResponse;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;

/**
 * Decorador de {@link TmdbClient} que protege todas las llamadas a la API de TMDB
 * con un <strong>circuit breaker</strong>. Si TMDB falla o va lento muchas veces,
 * el circuito se abre y las llamadas dejan de ejecutarse hasta que se reintente
 * (evita saturar el servicio y responde rápido con error controlado).
 */
@RequiredArgsConstructor
public class TmdbClientCircuitBreakerDecorator implements TmdbClient {

    private final TmdbClient delegate;
    private final CircuitBreaker circuitBreaker;

    private <T> T run(Supplier<T> supplier) {
        return circuitBreaker.executeSupplier(supplier);
    }

    @Override
    public TmdbMovieDetails getMovieDetails(long tmdbId) {
        return run(() -> delegate.getMovieDetails(tmdbId));
    }

    @Override
    public TmdbMovieDetails getMovieDetails(long tmdbId, @Nullable String languageOrNull) {
        return run(() -> delegate.getMovieDetails(tmdbId, languageOrNull));
    }

    @Override
    public TmdbCredits getMovieCredits(long tmdbId) {
        return run(() -> delegate.getMovieCredits(tmdbId));
    }

    @Override
    public TmdbCredits getMovieCredits(long tmdbId, @Nullable String languageOrNull) {
        return run(() -> delegate.getMovieCredits(tmdbId, languageOrNull));
    }

    @Override
    public TmdbVideosResponse getMovieVideos(long tmdbId) {
        return run(() -> delegate.getMovieVideos(tmdbId));
    }

    @Override
    public TmdbVideosResponse getMovieVideos(long tmdbId, String languageOrNull, @Nullable String includeVideoLanguageOrNull) {
        return run(() -> delegate.getMovieVideos(tmdbId, languageOrNull, includeVideoLanguageOrNull));
    }

    @Override
    public TmdbVideosResponse getMovieVideos(long tmdbId, String lang) {
        return run(() -> delegate.getMovieVideos(tmdbId, lang));
    }

    @Override
    public TmdbReleaseDatesResponse getMovieReleaseDates(long tmdbId) {
        return run(() -> delegate.getMovieReleaseDates(tmdbId));
    }

    @Override
    public TmdbPopularResponse getPopular(int page) {
        return run(() -> delegate.getPopular(page));
    }

    @Override
    public TmdbPersonDetails getPersonDetails(long personId) {
        return run(() -> delegate.getPersonDetails(personId));
    }

    @Override
    public TmdbPersonDetails getPersonDetails(long personId, String language) {
        return run(() -> delegate.getPersonDetails(personId, language));
    }
}
