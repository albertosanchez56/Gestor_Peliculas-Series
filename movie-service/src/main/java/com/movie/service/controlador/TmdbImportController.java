// src/main/java/com/movie/service/controlador/TmdbImportController.java
package com.movie.service.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.movie.service.Entidades.Movie;
import com.movie.service.tmdb.TmdbImportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tmdb")
@RequiredArgsConstructor
public class TmdbImportController {

  private final TmdbImportService importService;

  @PostMapping("/import/{tmdbId}")
  public ResponseEntity<Movie> importOne(@PathVariable long tmdbId) {
    return ResponseEntity.ok(importService.importMovie(tmdbId));
  }

  @PostMapping("/import/popular")
  public ResponseEntity<List<Movie>> importPopular(@RequestParam(defaultValue = "1") int pages) {
    return ResponseEntity.ok(importService.importPopular(pages));
  }
}
