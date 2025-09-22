package com.movie.service.controlador;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.movie.service.DTO.DirectorDTO;
import com.movie.service.DTO.GenreDTO;
import com.movie.service.DTO.MovieDTO;
import com.movie.service.DTO.MovieRequest;
import com.movie.service.Entidades.Movie;
import com.movie.service.servicio.MovieService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
// Para ResponseStatusException

@RestController
@RequestMapping("/peliculas")
@RequiredArgsConstructor
@Validated
public class MovieController {

	private final MovieService movieService;

	// GET /peliculas/mostrarpeliculas
	@GetMapping(value = "/mostrarpeliculas", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovieDTO>> listarPeliculas(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "50") int size) {
		// aunque mantengas la ruta “no REST”, puedes paginar por querystring
		List<Movie> peliculas = movieService.getAll(page, size);
		return ResponseEntity.ok(peliculas.stream().map(this::toDto).toList());
	}

	// POST /peliculas/guardarpeliculas
	@PostMapping(value = "/guardarpeliculas", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieDTO> crearPelicula(@Valid @RequestBody MovieRequest req, // evita exponer la entidad
			UriComponentsBuilder uri) {
		Movie created = movieService.create(req); // la lógica de mapping vive en el service
		URI location = uri.path("/peliculas/peliculas/{id}").buildAndExpand(created.getId()).toUri();
		return ResponseEntity.created(location).body(toDto(created)); // 201 + Location
	}

	// GET /peliculas/peliculas/{id}
	@GetMapping(value = "/peliculas/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieDTO> obtenerPorId(@PathVariable Long id) {
		Movie m = movieService.getByIdOrThrow(id); // lanza NotFound si no existe
		return ResponseEntity.ok(toDto(m));
	}

	// PUT /peliculas/peliculas/{id}
	@PutMapping(value = "/peliculas/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MovieRequest req) {
		Movie updated = movieService.update(id, req);
		return ResponseEntity.ok(toDto(updated));
	}

	// DELETE /peliculas/peliculas/{id}
	@DeleteMapping("/peliculas/{id}")
	public ResponseEntity<Void> borrar(@PathVariable Long id) {
		movieService.delete(id);
		return ResponseEntity.noContent().build(); // 204
	}

	// ---------- Helpers privados (DRY) ----------

	private MovieDTO toDto(Movie m) {
		DirectorDTO dir = (m.getDirector() == null) ? null
				: new DirectorDTO(m.getDirector().getId(), m.getDirector().getName());

		List<GenreDTO> genres = (m.getGenres() == null) ? List.of()
				: m.getGenres().stream().map(g -> new GenreDTO(g.getId(), g.getName())).toList();

		return new MovieDTO(m.getId(), m.getTitle(), m.getDescription(), m.getReleaseDate(), dir, genres);
	}
}
