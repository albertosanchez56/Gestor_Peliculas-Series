package com.movie.service.servicio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movie.service.DTO.MovieRequest;
import com.movie.service.Entidades.Director;
import com.movie.service.Entidades.Genre;
import com.movie.service.Entidades.Movie;
import com.movie.service.repositorio.DirectorRepository;
import com.movie.service.repositorio.GenreRepository;
import com.movie.service.repositorio.MovieRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final GenreRepository genreRepository;

    /* ===========================
       Métodos antiguos (compat)
       =========================== */

    public List<Movie> getAll() {
        return movieRepository.findAll();
    }

    // antes: Optional<Movie> obtenerPelicula(int id)
    public java.util.Optional<Movie> obtenerPelicula(Long id) {
        return movieRepository.findById(id);
    }

    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    public void borrarPelicula(Movie movie) {
        movieRepository.delete(movie);
    }
    


    /* ===========================
       Métodos nuevos (controller)
       =========================== */

    /** Lista con paginación simple manteniendo tu ruta actual. */
    public List<Movie> getAll(int page, int size) {
        List<Movie> all = movieRepository.findAll();
        if (all.isEmpty()) return List.of();

        int from = Math.max(0, page * size);
        if (from >= all.size()) return List.of();

        int to = Math.min(all.size(), from + size);
        return new ArrayList<>(all.subList(from, to));
    }

    /** Obtiene o lanza 404. */
    public Movie getByIdOrThrow(Long id) {
        return movieRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("No existe la pelicula con el ID: " + id));
    }

    /** Crea desde MovieRequest (sin exponer entidad en el controller). */
    @Transactional
    public Movie create(MovieRequest req) {
        Movie m = new Movie();
        applyRequestToEntity(m, req);
        return movieRepository.save(m);
    }

    /** Actualiza desde MovieRequest. */
    @Transactional
    public Movie update(Long id, MovieRequest req) {
        Movie m = movieRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("No existe la pelicula con el ID: " + id));
        applyRequestToEntity(m, req);
        return movieRepository.save(m);
    }

    /** Borra por id. */
    @Transactional
    public void delete(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException("No existe la pelicula con el ID: " + id);
        }
        movieRepository.deleteById(id);
    }
    
    public List<Movie> getTopRated(int limit) {
    	  int lim = Math.max(1, Math.min(limit, 50)); // cap opcional
    	  return movieRepository.findTopRated(PageRequest.of(0, lim));
    	}

    /** Top valoradas filtradas por slug de género (ej. "accion"). */
    public List<Movie> getTopRatedByGenre(String genreSlug, int limit) {
        if (genreSlug == null || genreSlug.isBlank()) return List.of();
        int lim = Math.max(1, Math.min(limit, 50));
        Sort sort = Sort.by(Sort.Direction.DESC, "averageRating")
            .and(Sort.by(Sort.Direction.DESC, "voteCount"));
        return movieRepository.findTopRatedByGenreSlug(genreSlug.trim(), PageRequest.of(0, lim, sort));
    }
    
    public Page<Movie> listPaged(int page, int size, @Nullable String q) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        if (q == null || q.isBlank()) {
            return movieRepository.findAll(pageable);
        }
        return movieRepository.searchAllFields(q.trim(), pageable);
        // O si prefieres simple por título:
        // return movieRepository.findByTitleContainingIgnoreCase(q.trim(), pageable);
    }

    /** Sugerencias (autocomplete) limitado por size. */
    public List<Movie> searchByText(String q, int size) {
        if (q == null || q.isBlank()) return List.of();
        Pageable pageable = PageRequest.of(0, size);
        return movieRepository.searchAllFields(q.trim(), pageable).getContent();
    }


    /* ===========================
       Helper de mapeo
       =========================== */

    private void applyRequestToEntity(Movie m, MovieRequest req) {
    	m.setTitle(req.title());
        m.setDescription(req.description());
        m.setReleaseDate(req.releaseDate());

        // director y géneros (como ya tienes)
        var director = directorRepository.findById(req.directorId())
            .orElseThrow(() -> new EntityNotFoundException("No existe el director con ID: " + req.directorId()));
        m.setDirector(director);

        var genres = new java.util.HashSet<Genre>();
        for (Long gid : req.genreIds()) {
            genres.add(genreRepository.findById(gid)
                .orElseThrow(() -> new EntityNotFoundException("No existe el género con ID: " + gid)));
        }
        m.setGenres(genres);

        // --- nuevos (si vienen) ---
        if (req.durationMinutes() != null)   m.setDurationMinutes(req.durationMinutes());
        if (req.originalLanguage() != null)  m.setOriginalLanguage(req.originalLanguage());
        if (req.posterUrl() != null)         m.setPosterUrl(req.posterUrl());
        if (req.backdropUrl() != null)       m.setBackdropUrl(req.backdropUrl());
        if (req.trailerUrl() != null)        m.setTrailerUrl(req.trailerUrl());
        if (req.ageRating() != null)         m.setAgeRating(req.ageRating());
    }
    
    @Transactional
    public void updateAggregates(Long id, Double avg, Integer count) {
        Movie m = movieRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("No existe la pelicula con el ID: " + id));

        m.setAverageRating(avg);
        m.setVoteCount(count);

        movieRepository.save(m);
    }

}
