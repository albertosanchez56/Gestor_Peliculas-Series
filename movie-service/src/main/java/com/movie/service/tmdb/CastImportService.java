// src/main/java/com/movie/service/tmdb/CastImportService.java
package com.movie.service.tmdb;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movie.service.Entidades.CastCredit;
import com.movie.service.Entidades.Movie;
import com.movie.service.configuracion.TmdbProps;
import com.movie.service.repositorio.CastCreditRepository;
import com.movie.service.repositorio.MovieRepository;
import com.movie.service.tmdb.dto.TmdbCredits;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CastImportService {

  private final TmdbClient tmdbClient;
  private final TmdbProps props;
  private final MovieRepository movieRepository;
  private final CastCreditRepository castRepo;

  /**
   * Refresca el cast de una película desde TMDB:
   * - Borra el reparto anterior.
   * - Carga créditos desde TMDB.
   * - Filtra a actores con personaje válido.
   * - Ordena por TMDB 'order' y luego por popularidad.
   * - Deduplica por (movie_id, tmdb_person_id) y, si no hay id, por (nombre+personaje).
   * - Inserta el nuevo reparto con orderIndex secuencial.
   */
  @Transactional
  public List<CastCredit> refreshCast(Long movieId, long tmdbMovieId) {
    Movie movie = movieRepository.findById(movieId)
        .orElseThrow(() -> new IllegalArgumentException("Movie no encontrada: " + movieId));

    // 1) Obtener créditos de TMDB
    TmdbCredits credits = tmdbClient.getMovieCredits(tmdbMovieId);
    if (credits == null || credits.cast() == null || credits.cast().isEmpty()) {
      // limpiar por si hubiera datos previos y devolver vacío
      castRepo.deleteByMovieId(movieId);
      return List.of();
    }

    // 2) Filtrar y ordenar (top N)
    List<TmdbCredits.Cast> sorted = credits.cast().stream()
        .filter(c -> "Acting".equalsIgnoreCase(c.known_for_department()))
        .filter(c -> c.name() != null && !c.name().isBlank())
        .filter(c -> c.character() != null && !c.character().isBlank())
        .sorted(Comparator
            .comparing((TmdbCredits.Cast c) -> c.order() == null ? Integer.MAX_VALUE : c.order())
            .thenComparing(c -> c.popularity() == null ? 0.0 : -c.popularity()))
        .limit(50) // ajusta si quieres mostrar más/menos
        .toList();

    // 3) DEDUP:
    //    - Con tmdb_person_id: quedarnos con el primero (menor 'order').
    //    - Sin tmdb_person_id: deduplicar por (nombre|personaje).
    Map<Long, TmdbCredits.Cast> byPersonId = new LinkedHashMap<>();
    Map<String, TmdbCredits.Cast> byNameCharacter = new LinkedHashMap<>();
    for (TmdbCredits.Cast c : sorted) {
      Long pid = c.id(); // Asegúrate de que tu DTO tiene Long id()
      if (pid != null) {
        byPersonId.putIfAbsent(pid, c);
      } else {
        String key = (nvl(c.name()) + "|" + nvl(c.character())).toLowerCase();
        byNameCharacter.putIfAbsent(key, c);
      }
    }

    // 4) Borrar reparto anterior
    castRepo.deleteByMovieId(movieId);
    castRepo.flush();

    // 5) Construir entidades y asignar orderIndex secuencial
    List<CastCredit> toSave = new ArrayList<>();
    int orderIndex = 0;

    for (TmdbCredits.Cast c : byPersonId.values()) {
      CastCredit e = new CastCredit();
      e.setMovie(movie);
      e.setTmdbPersonId(c.id());
      e.setPersonName(nvl(c.name()));
      e.setCharacterName(nvl(c.character()));
      e.setKnownForDepartment(nvl(c.known_for_department()));
      e.setPopularity(c.popularity());
      e.setOrderIndex(orderIndex++);
      e.setProfileUrl(buildImageUrl(c.profilePath(), props.getProfileSize())); // tu DTO: profilePath()
      toSave.add(e);
    }

    for (TmdbCredits.Cast c : byNameCharacter.values()) {
      CastCredit e = new CastCredit();
      e.setMovie(movie);
      e.setTmdbPersonId(null); // no hay id TMDB
      e.setPersonName(nvl(c.name()));
      e.setCharacterName(nvl(c.character()));
      e.setKnownForDepartment(nvl(c.known_for_department()));
      e.setPopularity(c.popularity());
      e.setOrderIndex(orderIndex++);
      e.setProfileUrl(buildImageUrl(c.profilePath(), props.getProfileSize()));
      toSave.add(e);
    }
    var seenByPersonId = new java.util.HashSet<Long>();
    List<CastCredit> filtered = new ArrayList<>();
    for (var e : toSave) {
      Long pid = e.getTmdbPersonId();
      if (pid == null) {
        filtered.add(e); // los NULL no chocan en índices únicos
      } else if (seenByPersonId.add(pid)) {
        filtered.add(e); // primera vez que vemos ese personId
      }
    }

    // 6) Guardar y devolver
    return castRepo.saveAll(filtered);
  }

  /* ------------ helpers ------------ */

  private String nvl(String s) {
    return s == null ? "" : s;
  }

  private String buildImageUrl(String path, String size) {
    if (path == null || path.isBlank()) return null;
    return props.getImagesBase() + size + path;
  }
}
