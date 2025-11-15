// src/main/java/com/movie/service/tmdb/CastImportService.java
package com.movie.service.tmdb;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

  @Transactional
  public void refreshCast(Long movieId, long tmdbMovieId) {
    Movie movie = movieRepository.findById(movieId)
        .orElseThrow(() -> new IllegalArgumentException("Movie no encontrada: " + movieId));

    TmdbCredits credits = tmdbClient.getMovieCredits(tmdbMovieId);
    // Si no tienes aún el DTO con cast, amplíalo como te indiqué antes.
    List<TmdbCredits.Cast> cast = Optional.ofNullable(credits).map(TmdbCredits::cast).orElse(List.of());

    // ---- AQUÍ VA TU CÓDIGO ----
    var topCast = Optional.ofNullable(credits.cast()).orElse(List.of()).stream()
    	    .filter(c -> "Acting".equalsIgnoreCase(c.known_for_department()))
    	    .filter(c -> c.name() != null && !c.name().isBlank())        // <-- clave
    	    .filter(c -> c.character() != null && !c.character().isBlank())
    	    .sorted(
    	        java.util.Comparator
    	          .comparing((TmdbCredits.Cast c) -> c.order() == null ? Integer.MAX_VALUE : c.order())
    	          .thenComparing(c -> c.popularity() == null ? 0.0 : -c.popularity())
    	    )
    	    .limit(12)
    	    .toList();

    // Limpia cast anterior
    castRepo.deleteByMovieId(movieId);
    List<CastCredit> nuevos = new ArrayList<>();
    for (var c : topCast) {
        CastCredit e = new CastCredit();
        e.setMovie(movie); // ya cargado
        e.setTmdbPersonId(c.id());
        e.setPersonName(c.name());
        e.setCharacterName(c.character());
        e.setOrderIndex(c.order());
        e.setKnownForDepartment(c.known_for_department());        // <— AQUÍ
        e.setPopularity(c.popularity());                           // <— AQUÍ
        e.setProfileUrl(buildImageUrl(c.profile_path(), props.getProfileSize()));
        nuevos.add(e);
    }
    castRepo.saveAll(nuevos);
  }
  private String buildImageUrl(String path, String size) {
	  if (path == null || path.isBlank()) return null;
	  return props.getImagesBase() + size + path;
	}

  
}
