// src/main/java/com/movie/service/tmdb/TmdbClient.java
package com.movie.service.tmdb;

import com.movie.service.tmdb.dto.*;

public interface TmdbClient {
    TmdbMovieDetails getMovieDetails(long tmdbId);
    TmdbCredits getMovieCredits(long tmdbId);
    TmdbVideosResponse getMovieVideos(long tmdbId);
    TmdbReleaseDatesResponse getMovieReleaseDates(long tmdbId);
    TmdbPopularResponse getPopular(int page);
}
