package com.movie.service.servicio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    /* ===========================
       Helper de mapeo
       =========================== */

    private void applyRequestToEntity(Movie m, MovieRequest req) {
        m.setTitle(req.title());
        m.setDescription(req.description());
        m.setReleaseDate(req.releaseDate());

        // Director
        Director director = directorRepository.findById(req.directorId())
            .orElseThrow(() -> new EntityNotFoundException("No existe el director con ID: " + req.directorId()));
        m.setDirector(director);

        // Géneros
        var genres = new HashSet<Genre>();
        for (Long gid : req.genreIds()) {
            Genre g = genreRepository.findById(gid)
                .orElseThrow(() -> new EntityNotFoundException("No existe el género con ID: " + gid));
            genres.add(g);
        }
        m.setGenres(genres);
    }
}
