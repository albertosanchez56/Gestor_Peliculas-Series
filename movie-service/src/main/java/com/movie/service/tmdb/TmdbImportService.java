// src/main/java/com/movie/service/tmdb/TmdbImportService.java
package com.movie.service.tmdb;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movie.service.Entidades.Director;
import com.movie.service.Entidades.Genre;
import com.movie.service.Entidades.Movie;
import com.movie.service.configuracion.TmdbProps;
import com.movie.service.repositorio.DirectorRepository;
import com.movie.service.repositorio.GenreRepository;
import com.movie.service.repositorio.MovieRepository;
import com.movie.service.tmdb.dto.*;
import com.movie.service.util.SlugUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TmdbImportService {

  private final TmdbClient tmdbClient;
  private final TmdbProps props;
  private final MovieRepository movieRepository;
  private final DirectorRepository directorRepository;
  private final GenreRepository genreRepository;

  @Transactional
  public Movie importMovie(long tmdbId) {
    TmdbMovieDetails details = tmdbClient.getMovieDetails(tmdbId);
    if (details == null) {
      throw new IllegalArgumentException("TMDB no devolvió detalles para ID " + tmdbId);
    }

    Movie movie = movieRepository.findByTmdbId(tmdbId).orElse(new Movie());
    movie.setTmdbId(tmdbId);

    movie.setTitle(nvl(details.title()));
    movie.setDescription(nvl(details.overview()));
    movie.setReleaseDate(parseDate(details.releaseDate()));
    movie.setDurationMinutes(details.runtime());
    movie.setOriginalLanguage(nvl(details.originalLanguage()));
    movie.setPosterUrl(buildImageUrl(details.posterPath(), props.getPosterSize()));
    movie.setBackdropUrl(buildImageUrl(details.backdropPath(), props.getBackdropSize()));
    movie.setAverageRating(toDouble(details.voteAverage()));
    movie.setVoteCount(details.voteCount());

    TmdbVideosResponse videos = tmdbClient.getMovieVideos(tmdbId);
    movie.setTrailerUrl(selectBestTrailerUrl(videos));

    TmdbReleaseDatesResponse releases = tmdbClient.getMovieReleaseDates(tmdbId);
    movie.setAgeRating(selectCertification(releases, props.getCertCountry()));

    TmdbCredits credits = tmdbClient.getMovieCredits(tmdbId);
    Director director = findOrCreateDirector(credits).orElse(null);
    movie.setDirector(director);

    Set<Genre> genres = upsertGenres(details);
    movie.setGenres(genres);

    return movieRepository.save(movie);
  }

  @Transactional
  public List<Movie> importPopular(int pages) {
    pages = Math.max(1, Math.min(pages, 10));
    List<Movie> imported = new ArrayList<>();

    for (int page = 1; page <= pages; page++) {
      TmdbPopularResponse pr = tmdbClient.getPopular(page);
      if (pr == null || pr.results() == null) break;

      for (TmdbPopularResponse.Item item : pr.results()) {
        try {
          imported.add(importMovie(item.id()));
        } catch (Exception ex) {
          System.err.println("No se pudo importar TMDB " + item.id() + ": " + ex.getMessage());
        }
      }
    }
    return imported;
  }
  
  @Transactional
  public ImportSummary importPopularSummary(int pages) {
    pages = Math.max(1, Math.min(pages, 10));

    int requested = 0, created = 0, updated = 0, skipped = 0;
    List<Long> importedIds = new ArrayList<>();
    List<String> errors = new ArrayList<>();

    for (int page = 1; page <= pages; page++) {
      TmdbPopularResponse pr = tmdbClient.getPopular(page);
      if (pr == null || pr.results() == null) break;

      for (TmdbPopularResponse.Item item : pr.results()) {
        requested++;
        boolean existed = movieRepository.existsByTmdbId(item.id()); // antes de importar
        try {
          Movie saved = importMovie(item.id());
          if (existed) updated++; else created++;
          if (saved.getId() != null) importedIds.add(saved.getId());
        } catch (Exception ex) {
          skipped++;
          errors.add(item.id() + ": " + ex.getMessage());
        }
      }
    }
    return new ImportSummary(requested, created, updated, skipped, importedIds, errors);
  }


  /* Helpers */

  private String nvl(String s) { return s == null ? "" : s; }
  private Double toDouble(Number n) { return n == null ? null : n.doubleValue(); }

  private LocalDate parseDate(String yyyyMMdd) {
    try { return (yyyyMMdd == null || yyyyMMdd.isBlank()) ? null : LocalDate.parse(yyyyMMdd); }
    catch (Exception e) { return null; }
  }

  private String buildImageUrl(String path, String size) {
    if (path == null || path.isBlank()) return null;
    return props.getImagesBase() + size + path;
  }

  private String selectBestTrailerUrl(TmdbVideosResponse videos) {
    if (videos == null || videos.results() == null) return null;
    return videos.results().stream()
        .filter(v -> "YouTube".equalsIgnoreCase(v.site()))
        .filter(v -> "Trailer".equalsIgnoreCase(v.type()))
        .sorted(Comparator.comparing((TmdbVideosResponse.Video v) -> Boolean.TRUE.equals(v.official())).reversed())
        .findFirst()
        .map(v -> "https://www.youtube.com/watch?v=" + v.key())
        .orElse(null);
  }

  private String selectCertification(TmdbReleaseDatesResponse releases, String country) {
	  if (releases == null || releases.results() == null) return null;

	  return releases.results().stream()
	      .filter(r -> country != null && country.equalsIgnoreCase(r.iso_3166_1()))
	      .findFirst()
	      .map(TmdbReleaseDatesResponse.Result::release_dates)  // Optional<List<ReleaseDate>>
	      .filter(Objects::nonNull)
	      .stream()                                             // Optional -> Stream<List<ReleaseDate>>
	      .flatMap(List::stream)                                // List -> Stream<ReleaseDate>
	      .map(TmdbReleaseDatesResponse.ReleaseDate::certification)
	      .filter(c -> c != null && !c.isBlank())
	      .findFirst()
	      .orElse(null);
	}

  private Optional<Director> findOrCreateDirector(TmdbCredits credits) {
    if (credits == null || credits.crew() == null) return Optional.empty();
    return credits.crew().stream()
        .filter(c -> "Director".equalsIgnoreCase(c.job()))
        .findFirst()
        .map(c -> {
          String name = c.name();
          return directorRepository.findByNameIgnoreCase(name)
              .orElseGet(() -> {
                Director d = new Director();
                d.setName(name);
                return directorRepository.save(d);
              });
        });
  }

  private Set<Genre> upsertGenres(TmdbMovieDetails details) {
    if (details.genres() == null) return Collections.emptySet();
    Set<Genre> out = new HashSet<>();
    for (TmdbMovieDetails.TmdbGenre g : details.genres()) {
      String name = nvl(g.name());
      String slug = com.movie.service.util.SlugUtil.slugify(name);

      Genre genre = genreRepository.findBySlugIgnoreCase(slug)
          .orElseGet(() -> {
            Genre ng = new Genre();
            ng.setName(name);
            ng.setSlug(slug);
            return genreRepository.save(ng);
          });
      out.add(genre);
    }
    return out;
  }
}