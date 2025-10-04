// src/main/java/com/movie/service/controlador/TmdbImportController.java
package com.movie.service.controlador;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.movie.service.DTO.DirectorDTO;
import com.movie.service.DTO.GenreDTO;
import com.movie.service.DTO.MovieDTO;
import com.movie.service.Entidades.Director;
import com.movie.service.Entidades.Movie;
import com.movie.service.tmdb.TmdbImportService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tmdb")
@RequiredArgsConstructor
@Validated
public class TmdbImportController {

  private final TmdbImportService importService;

  // POST /tmdb/import/popular?pages=1..10
  @PostMapping(value = "/import/popular", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<MovieDTO>> importPopular(
      @RequestParam(defaultValue = "1") @Min(1) @Max(10) int pages) {

    List<Movie> imported = importService.importPopular(pages);
    List<MovieDTO> dto = imported.stream().map(this::toDto).toList();
    return ResponseEntity.ok(dto);
  }

  // POST /tmdb/import/{tmdbId}
  @PostMapping(value = "/import/{tmdbId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MovieDTO> importOne(@PathVariable @Positive long tmdbId) {
    Movie m = importService.importMovie(tmdbId); // <-- usa el método que SÍ tienes
    return ResponseEntity.ok(toDto(m));
  }
  
  

  // ------ mapper simple Movie -> MovieDTO (ajústalo si tu MovieDTO difiere)
  private MovieDTO toDto(Movie m) {
    Director d = m.getDirector();
    DirectorDTO dir = (d == null) ? null : new DirectorDTO(d.getId(), d.getName());

    List<GenreDTO> genres = (m.getGenres() == null)
        ? List.of()
        : m.getGenres().stream()
            .map(g -> new GenreDTO(g.getId(), g.getName(), List.of()))
            .toList();

    return new MovieDTO(
        m.getId(),
        m.getTitle(),
        m.getDescription(),
        m.getReleaseDate(),
        dir,
        genres,
        m.getDurationMinutes(),
        m.getOriginalLanguage(),
        m.getPosterUrl(),
        m.getBackdropUrl(),
        m.getTrailerUrl(),
        m.getAgeRating(),
        m.getAverageRating()
    );
  }
}
