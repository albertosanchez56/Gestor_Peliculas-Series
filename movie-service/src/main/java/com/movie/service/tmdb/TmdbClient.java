// src/main/java/com/movie/service/tmdb/TmdbClient.java
package com.movie.service.tmdb;

import com.movie.service.tmdb.dto.*;

public interface TmdbClient {
    TmdbMovieDetails getMovieDetails(long tmdbId);
    TmdbCredits getMovieCredits(long tmdbId);
    TmdbVideosResponse getMovieVideos(long tmdbId);
    TmdbReleaseDatesResponse getMovieReleaseDates(long tmdbId);
    TmdbPopularResponse getPopular(int page);
    
    TmdbMovieDetails getMovieDetails(long tmdbId, String languageOrNull);
    TmdbVideosResponse getMovieVideos(long tmdbId, String languageOrNull, String includeVideoLanguageOrNull);
    TmdbCredits getMovieCredits(long tmdbId, String languageOrNull);
    TmdbPersonDetails getPersonDetails(long personId);
    TmdbVideosResponse getMovieVideos(long tmdbId, String lang);
    TmdbPersonDetails getPersonDetails(long personId, String language);
}
