// src/main/java/com/movie/service/tmdb/TmdbImportService.java
package com.movie.service.tmdb;

import java.time.LocalDate;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movie.service.Entidades.Director;
import com.movie.service.Entidades.Genre;
import com.movie.service.Entidades.Movie;
import com.movie.service.Entidades.Movie.MovieStatus;
import com.movie.service.configuracion.TmdbProps;
import com.movie.service.repositorio.DirectorRepository;
import com.movie.service.repositorio.GenreRepository;
import com.movie.service.repositorio.MovieRepository;
import com.movie.service.tmdb.dto.*;
import com.movie.service.util.MovieSlugHelper;
import com.movie.service.util.SlugHelper;
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
  private final CastImportService castImportService;
  private final SlugHelper slugHelper;           // para director
  private final MovieSlugHelper movieSlugHelper; // para película

  @Transactional
  public Movie importMovie(long tmdbId) {
    TmdbMovieDetails details = tmdbClient.getMovieDetails(tmdbId);
    if (details == null) throw new IllegalArgumentException("TMDB no devolvió detalles para ID " + tmdbId);

    Movie movie = movieRepository.findByTmdbId(tmdbId).orElse(new Movie());
    boolean isNew = (movie.getId() == null);

    movie.setTmdbId(tmdbId);

    // Básicos
    movie.setTitle(nvl(details.title()));
    movie.setDescription(nvl(details.overview()));
    movie.setReleaseDate(parseDate(details.releaseDate()));
    movie.setDurationMinutes(details.runtime());
    movie.setOriginalLanguage(nvl(details.originalLanguage()));
    movie.setPosterUrl(buildImageUrl(details.posterPath(), props.getPosterSize()));
    movie.setBackdropUrl(buildImageUrl(details.backdropPath(), props.getBackdropSize()));
    movie.setAverageRating(toDouble(details.voteAverage()));
    movie.setVoteCount(details.voteCount());

    // Extras (asegúrate de que tu DTO trae estos campos)
    movie.setBudget(details.budget());
    movie.setRevenue(details.revenue());
    movie.setPopularity(details.popularity());
    movie.setOriginCountry(firstIsoCountry(details.productionCountries(), props.getCertCountry()));
    movie.setStatus(mapStatus(details.status()));

    // Slug único (si está vacío o “n-a”)
    if (isBlank(movie.getSlug()) || "n-a".equalsIgnoreCase(movie.getSlug())) {
      String yearHint = (details.releaseDate() != null && details.releaseDate().length() >= 4)
          ? details.releaseDate().substring(0, 4)
          : null;
      movie.setSlug(movieSlugHelper.uniqueMovieSlug(movie.getTitle(), yearHint, tmdbId));
    }

    // Trailer con fallbacks de idioma
    String trailer = pickTrailerViaFallbacks(tmdbId);
    movie.setTrailerUrl(trailer);

    // Age rating (certificación)
    TmdbReleaseDatesResponse releases = tmdbClient.getMovieReleaseDates(tmdbId);
    movie.setAgeRating(selectCertification(releases, props.getCertCountry()));

    // Director (con fallbacks de idioma para biografía, etc.)
    TmdbCredits credits = tmdbClient.getMovieCredits(tmdbId, props.getLanguage()); // es-ES
    if (credits == null || (credits.crew() == null && credits.cast() == null)) {
      credits = tmdbClient.getMovieCredits(tmdbId, "en-US"); // fallback EN
    }
    Director director = findOrCreateDirector(credits).orElse(null);
    movie.setDirector(director);

    // Géneros
    Set<Genre> genres = upsertGenres(details);
    movie.setGenres(genres);

    // 1) Guardar película
    Movie saved = movieRepository.save(movie);

    // 2) Refrescar cast (ya con movieId persistido)
    try {
      castImportService.refreshCast(saved.getId(), tmdbId);
    } catch (Exception ex) {
      System.err.println("No se pudo refrescar cast para movieId=" + saved.getId()
          + " tmdbId=" + tmdbId + ": " + ex.getMessage());
    }

    return saved;
  }

  /* ---------- Helpers de mapeo ---------- */

  private String firstIsoCountry(List<TmdbMovieDetails.ProductionCountry> pcs, String fallbackCountry) {
    if (pcs != null && !pcs.isEmpty()) {
      var iso = pcs.get(0).iso_3166_1();
      if (iso != null && !iso.isBlank()) return iso;
    }
    return (fallbackCountry != null && !fallbackCountry.isBlank()) ? fallbackCountry : null;
  }

  private MovieStatus mapStatus(String tmdbStatus) {
    if (tmdbStatus == null) return null;
    return switch (tmdbStatus.toLowerCase()) {
      case "released" -> MovieStatus.RELEASED;
      case "rumored" -> MovieStatus.RUMORED;
      case "planned" -> MovieStatus.PLANNED;
      case "in production" -> MovieStatus.IN_PRODUCTION;
      case "post production" -> MovieStatus.POST_PRODUCTION;
      case "canceled", "cancelled" -> MovieStatus.CANCELED;
      default -> null;
    };
  }

  private String pickTrailerViaFallbacks(long tmdbId) {
    // 1) idioma preferido
    String pref = props.getLanguage(); // p.ej. es-ES
    String url = pickFromVideos(tmdbClient.getMovieVideos(tmdbId, pref));
    if (url != null) return url;

    // 2) en-US
    url = pickFromVideos(tmdbClient.getMovieVideos(tmdbId, "en-US"));
    if (url != null) return url;

    // 3) en “genérico” (si tu cliente no admite “sin language”)
    url = pickFromVideos(tmdbClient.getMovieVideos(tmdbId, "en"));
    return url;
  }

  private String pickFromVideos(TmdbVideosResponse videos) {
    if (videos == null || videos.results() == null) return null;

    // Trailer oficial en YouTube
    Optional<TmdbVideosResponse.Video> bestTrailer = videos.results().stream()
        .filter(v -> "YouTube".equalsIgnoreCase(v.site()))
        .filter(v -> "Trailer".equalsIgnoreCase(v.type()))
        .sorted((a, b) -> Boolean.compare(Boolean.TRUE.equals(b.official()), Boolean.TRUE.equals(a.official())))
        .findFirst();

    if (bestTrailer.isPresent()) {
      return "https://www.youtube.com/watch?v=" + bestTrailer.get().key();
    }

    // Fallback: Teaser o Clip
    Optional<TmdbVideosResponse.Video> teaserOrClip = videos.results().stream()
        .filter(v -> "YouTube".equalsIgnoreCase(v.site()))
        .filter(v -> {
          String t = v.type();
          return "Teaser".equalsIgnoreCase(t) || "Clip".equalsIgnoreCase(t);
        })
        .findFirst();

    return teaserOrClip.map(v -> "https://www.youtube.com/watch?v=" + v.key()).orElse(null);
  }

  /* ---------- Importaciones masivas ---------- */

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
        boolean existed = movieRepository.existsByTmdbId(item.id());
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

  /* ---------- Helpers genéricos ---------- */

  private String nvl(String s) { return s == null ? "" : s; }
  

  private LocalDate parseDate(String yyyyMMdd) {
    try { return (yyyyMMdd == null || yyyyMMdd.isBlank()) ? null : LocalDate.parse(yyyyMMdd); }
    catch (Exception e) { return null; }
  }

  private String buildImageUrl(String path, String size) {
    if (path == null || path.isBlank()) return null;
    return props.getImagesBase() + size + path;
  }

  private String selectCertification(TmdbReleaseDatesResponse releases, String country) {
    if (releases == null || releases.results() == null) return null;

    return releases.results().stream()
        .filter(r -> country != null && country.equalsIgnoreCase(r.iso_3166_1()))
        .findFirst()
        .map(TmdbReleaseDatesResponse.Result::release_dates)
        .filter(Objects::nonNull)
        .stream()
        .flatMap(List::stream)
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
          Director d = null;
          Long tmdbPersonId = null;

          // tu DTO Crew debe tener Long id(), String name(), y si puedes profilePath()
          if (c.id() != null) {
            tmdbPersonId = c.id();
            d = directorRepository.findByTmdbId(c.id()).orElse(null);
          }
          if (d == null && c.name() != null && !c.name().isBlank()) {
            d = directorRepository.findByNameIgnoreCase(c.name()).orElse(null);
          }
          if (d == null) d = new Director();

          // básicos
          if (!isBlank(c.name())) d.setName(c.name());
          if (tmdbPersonId != null) d.setTmdbId(tmdbPersonId);

          // Fall-backs de persona (ES → EN → sin idioma)
          TmdbPersonDetails pEs = (tmdbPersonId != null)
              ? tmdbClient.getPersonDetails(tmdbPersonId, props.getLanguage())
              : null;

          String bio = (pEs != null) ? emptyToNull(pEs.biography()) : null;
          String birthday = (pEs != null) ? pEs.birthday() : null;
          String deathday = (pEs != null) ? pEs.deathday() : null;
          String place = (pEs != null) ? pEs.placeOfBirth() : null;
          String profile = (pEs != null) ? pEs.profilePath() : null;
          String imdb = (pEs != null) ? emptyToNull(pEs.imdbId()) : null;

          if (isBlank(bio) || isBlank(profile) || isBlank(place)) {
            TmdbPersonDetails pEn = (tmdbPersonId != null)
                ? tmdbClient.getPersonDetails(tmdbPersonId, "en-US")
                : null;
            if (pEn != null) {
              bio = firstNonBlank(bio, emptyToNull(pEn.biography()));
              birthday = firstNonBlank(birthday, pEn.birthday());
              deathday = firstNonBlank(deathday, pEn.deathday());
              place = firstNonBlank(place, pEn.placeOfBirth());
              profile = firstNonBlank(profile, pEn.profilePath());
              imdb = firstNonBlank(imdb, emptyToNull(pEn.imdbId()));
            }
          }

          if (isBlank(bio) || isBlank(profile) || isBlank(place)) {
            TmdbPersonDetails pNoLang = (tmdbPersonId != null)
                ? tmdbClient.getPersonDetails(tmdbPersonId, null)
                : null;
            if (pNoLang != null) {
              bio = firstNonBlank(bio, emptyToNull(pNoLang.biography()));
              birthday = firstNonBlank(birthday, pNoLang.birthday());
              deathday = firstNonBlank(deathday, pNoLang.deathday());
              place = firstNonBlank(place, pNoLang.placeOfBirth());
              profile = firstNonBlank(profile, pNoLang.profilePath());
              imdb = firstNonBlank(imdb, emptyToNull(pNoLang.imdbId()));
            }
          }

          // Asignar solo si están vacíos actualmente
          if (isBlank(d.getBiography()) && !isBlank(bio)) d.setBiography(bio);
          if (d.getBirthDate() == null && !isBlank(birthday)) d.setBirthDate(parseDate(birthday));
          if (d.getDeathDate() == null && !isBlank(deathday)) d.setDeathDate(parseDate(deathday));
          if (isBlank(d.getNationality()) && !isBlank(place)) d.setNationality(extractCountry(place));
          if (isBlank(d.getPhotoUrl()) && !isBlank(profile)) d.setPhotoUrl(buildImageUrl(profile, props.getProfileSize()));
          if (isBlank(d.getImdbId()) && !isBlank(imdb)) d.setImdbId(imdb);

          // slug robusto para director
          if (isBlank(d.getSlug()) || "n-a".equalsIgnoreCase(d.getSlug())) {
            d.setSlug(slugHelper.uniqueDirectorSlug(d.getName(), tmdbPersonId));
          }

          return directorRepository.save(d);
        });
  }

  private String emptyToNull(String s){ return (s == null || s.isBlank()) ? null : s; }

  private String extractCountry(String placeOfBirth) {
    if (placeOfBirth == null || placeOfBirth.isBlank()) return null;
    var parts = placeOfBirth.split(",");
    return parts.length == 0 ? null : parts[parts.length - 1].trim();
  }

  private Set<Genre> upsertGenres(TmdbMovieDetails details) {
    if (details.genres() == null) return Collections.emptySet();
    Set<Genre> out = new HashSet<>();
    for (TmdbMovieDetails.TmdbGenre g : details.genres()) {
      String name = nvl(g.name());
      String slug = SlugUtil.slugify(name);

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

  private static boolean isBlank(String s){ return s == null || s.isBlank(); }
  private static String firstNonBlank(String... arr){ for (String s: arr) if (!isBlank(s)) return s; return null; }
  private static Double toDouble(Number n){ return n == null ? null : n.doubleValue(); }
}
