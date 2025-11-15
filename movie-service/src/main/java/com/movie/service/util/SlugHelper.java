package com.movie.service.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.movie.service.repositorio.DirectorRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlugHelper {
  private final DirectorRepository directorRepository;

  public String uniqueDirectorSlug(String base, Long tmdbId) {
    String candidate = (base == null || base.isBlank())
        ? (tmdbId != null ? "director-" + tmdbId : "director-" + UUID.randomUUID())
        : SlugUtil.slugify(base);

    if (candidate.isBlank() || "n-a".equals(candidate)) {
      candidate = (tmdbId != null ? "director-" + tmdbId : "director-" + UUID.randomUUID());
    }

    if (!directorRepository.existsBySlugIgnoreCase(candidate)) {
      return candidate;
    }
    // Resolver colisiones: intenta con tmdbId y contador
    if (tmdbId != null) {
      String c2 = candidate + "-" + tmdbId;
      if (!directorRepository.existsBySlugIgnoreCase(c2)) return c2;
    }
    int i = 2;
    String next = candidate + "-" + i;
    while (directorRepository.existsBySlugIgnoreCase(next)) {
      i++;
      next = candidate + "-" + i;
    }
    return next;
  }
}
