package com.movie.service.controlador;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.movie.service.DTO.DirectorDTO;
import com.movie.service.DTO.GenreDTO;
import com.movie.service.DTO.MovieDTO;
import com.movie.service.Entidades.Director;
import com.movie.service.Entidades.Genre;
import com.movie.service.Entidades.Movie;
import com.movie.service.servicio.GenreService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/generos")
@RequiredArgsConstructor
@Validated
public class GenreController {

  private final GenreService genreService;

  // GET /peliculas/mostrargeneros
  @GetMapping(value="/mostrargeneros", produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<GenreDTO>> listarGeneros(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size
  ) {
    List<Genre> generos = genreService.getAll(page, size);
    return ResponseEntity.ok(generos.stream().map(this::toDto).toList());
  }

  // POST /peliculas/guardargeneros  (aceptando entidad Genre por compatibilidad)
  @PostMapping(value="/guardargeneros", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GenreDTO> crearGenero(@RequestBody Genre genero, UriComponentsBuilder uri) {
    Genre created = genreService.save(genero);
    URI location = uri.path("/peliculas/generos/{id}").buildAndExpand(created.getId()).toUri();
    return ResponseEntity.created(location).body(toDto(created));
  }

  // GET /peliculas/generos/{id}
  @GetMapping(value="/generos/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GenreDTO> obtenerPorId(@PathVariable Long id) {
    Genre genero = genreService.getByIdOrThrow(id);
    return ResponseEntity.ok(toDto(genero));
  }

  // PUT /peliculas/generos/{id}  (aceptando entidad Genre por compatibilidad)
  @PutMapping(value="/generos/{id}", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GenreDTO> actualizar(@PathVariable Long id, @RequestBody Genre body) {
    Genre updated = genreService.updateName(id, body.getName());
    return ResponseEntity.ok(toDto(updated));
  }

  // DELETE /peliculas/generos/{id}
  @DeleteMapping("/generos/{id}")
  public ResponseEntity<Void> borrar(@PathVariable Long id) {
    genreService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // GET /peliculas/generosexists/{name}
  @GetMapping("/generosexists/{name}")
  public ResponseEntity<Boolean> exists(@PathVariable String name) {
    return ResponseEntity.ok(genreService.existsByName(name));
  }

  // ----- mapper privado: Genre -> GenreDTO (incluye películas) -----
  private GenreDTO toDto(Genre g) {
    List<MovieDTO> movies = Optional.ofNullable(g.getMovies())
        .orElse(Collections.emptySet())
        .stream()
        .map(m -> {
          Director d = m.getDirector();
          DirectorDTO dirDto = (d == null) ? null : new DirectorDTO(d.getId(), d.getName());
          List<GenreDTO> genreDtos = Optional.ofNullable(m.getGenres())
              .orElse(Collections.emptySet())
              .stream()
              .map(x -> new GenreDTO(x.getId(), x.getName(), Collections.emptyList()))
              .toList();
          return new MovieDTO(
              m.getId(),
              m.getTitle(),
              m.getDescription(),
              m.getReleaseDate(),
              dirDto,
              genreDtos
          );
        }).toList();

    return new GenreDTO(g.getId(), g.getName(), movies);
  }
}
