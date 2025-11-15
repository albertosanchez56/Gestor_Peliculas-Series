package com.movie.service.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.movie.service.repositorio.MovieRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MovieSlugHelper {
  private final MovieRepository movieRepository;

  public String uniqueMovieSlug(String title, String yearHint, Long tmdbId) {
    String base = (title == null || title.isBlank()) ? null : title;
    String candidate = (base == null) ? null : com.movie.service.util.SlugUtil.slugify(
        (yearHint == null || yearHint.isBlank()) ? base : (base + "-" + yearHint)
    );

    if (candidate == null || candidate.isBlank() || "n-a".equals(candidate)) {
      candidate = (tmdbId != null ? "movie-" + tmdbId : "movie-" + UUID.randomUUID());
    }

    if (!movieRepository.existsBySlugIgnoreCase(candidate)) return candidate;

    if (tmdbId != null) {
      String c2 = candidate + "-" + tmdbId;
      if (!movieRepository.existsBySlugIgnoreCase(c2)) return c2;
    }

    int i = 2;
    String next = candidate + "-" + i;
    while (movieRepository.existsBySlugIgnoreCase(next)) {
      i++;
      next = candidate + "-" + i;
    }
    return next;
  }
}