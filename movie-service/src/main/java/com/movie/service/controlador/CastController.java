// src/main/java/com/movie/service/tmdb/CastController.java
package com.movie.service.controlador;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.movie.service.Entidades.CastCredit;
import com.movie.service.repositorio.CastCreditRepository;
import com.movie.service.tmdb.CastImportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class CastController {

	  private final CastImportService castImportService;
	  private final CastCreditRepository castRepository;

	  // Admin/dev: refrescar reparto desde TMDB
	  @PostMapping("/{movieId}/tmdb/{tmdbId}/import-cast")
	  public List<CastCredit> importCast(@PathVariable Long movieId,
	                                     @PathVariable Long tmdbId) {
	    castImportService.refreshCast(movieId, tmdbId); // <- sigue siendo void
	    // Ahora leemos y devolvemos el reparto ordenado
	    return castRepository.findByMovieIdOrderByOrderIndexAsc(movieId);
	  }

	  // Público: obtener reparto ordenado
	  @GetMapping("/{movieId}/cast")
	  public List<CastCredit> getCast(@PathVariable Long movieId) {
	    return castRepository.findByMovieIdOrderByOrderIndexAsc(movieId);
	  }
	}
