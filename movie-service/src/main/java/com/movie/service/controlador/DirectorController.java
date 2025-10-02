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
import com.movie.service.Entidades.Movie;
import com.movie.service.servicio.DirectorService;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/directores")
@RequiredArgsConstructor
@Validated
public class DirectorController {

  private final DirectorService directorService;

  // GET /directores/mostrardirectores
  @GetMapping(value = "/mostrardirectores", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DirectorDTO>> listarDirectores(
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "50") int size
  ) {
    List<Director> directores = directorService.getAll();
    // paginado simple como en películas
    int from = Math.max(0, page * size);
    int to   = Math.min(directores.size(), from + size);
    if (from >= to) return ResponseEntity.ok(List.of());
    var dtos = directores.subList(from, to).stream().map(this::toDto).toList();
    return ResponseEntity.ok(dtos);
  }

  // POST /directores/guardardirectores
  // (aceptamos entidad Director por compatibilidad con tu Angular actual)
  @PostMapping(value = "/guardardirectores",
               consumes = MediaType.APPLICATION_JSON_VALUE,
               produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DirectorDTO> crearDirector(@RequestBody Director body, UriComponentsBuilder uri) {
    Director created = directorService.save(body);
    URI location = uri.path("/directores/directores/{id}").buildAndExpand(created.getId()).toUri();
    return ResponseEntity.created(location).body(toDto(created));
  }

  // GET /directores/directores/{id}
  @GetMapping(value = "/directores/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DirectorDTO> obtenerPorId(@PathVariable Long id) {
    return directorService.findByIdDto(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No existe el director con el ID: " + id));
  }

  // PUT /directores/directores/{id}
  // (aceptamos entidad Director y actualizamos solo el nombre)
  @PutMapping(value = "/directores/{id}",
              consumes = MediaType.APPLICATION_JSON_VALUE,
              produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DirectorDTO> actualizar(@PathVariable Long id, @RequestBody Director body) {
    // reutilizamos tu updateDto, pasando solo el nombre
    DirectorDTO updated = directorService.updateDto(id, new DirectorDTO(id, body.getName()));
    return ResponseEntity.ok(updated);
  }

  // DELETE /directores/directores/{id}
  @DeleteMapping("/directores/{id}")
  public ResponseEntity<Void> borrar(@PathVariable Long id) {
    // usamos el flujo que ya te funcionaba sin query @Modifying
    Director d = directorService.obtenerDirector(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No existe el director con el ID: " + id));
    directorService.borrarDirector(d);
    return ResponseEntity.noContent().build();
  }
  
//--- reutiliza tu mismo slim mapper de películas ---
private MovieDTO toMovieSlimDto(Movie m) {
   Director d = m.getDirector();
   DirectorDTO dirDto = (d == null) ? null : new DirectorDTO(d.getId(), d.getName());

   return new MovieDTO(
       m.getId(),
       m.getTitle(),
       m.getDescription(),
       m.getReleaseDate(),
       dirDto,
       List.of(),               // géneros vacíos: evita recursión
       m.getDurationMinutes(),
       m.getOriginalLanguage(),
       m.getPosterUrl(),
       m.getBackdropUrl(),
       m.getTrailerUrl(),
       m.getAgeRating(),
       m.getAverageRating()
   );
}

  // ---------- Mapper privado (Director -> DirectorDTO) ----------
private DirectorDTO toDto(Director d) {
    List<MovieDTO> movies = Optional.ofNullable(d.getMovies())
        .orElse(List.of())
        .stream()
        .map(this::toMovieSlimDto)
        .toList();

    // Si tu DirectorDTO es (id, name, movies):
    return new DirectorDTO(d.getId(), d.getName(), movies);
  }
}
