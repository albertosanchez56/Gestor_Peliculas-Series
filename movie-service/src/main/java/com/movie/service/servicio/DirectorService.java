package com.movie.service.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movie.service.DTO.DirectorDTO;
import com.movie.service.Entidades.Director;
import com.movie.service.Entidades.Movie;
import com.movie.service.repositorio.DirectorRepository;
import com.movie.service.repositorio.MovieRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectorService {

    private final DirectorRepository directorRepository;
    private final MovieRepository movieRepository;

    /* ===========================
       Métodos antiguos (compat)
       =========================== */

    /** Lista completa (ordenada por nombre). */
    public List<Director> getAll() {
        return directorRepository.findAll(Sort.by("name").ascending());
    }

    /** Obtener Optional por id (Long). */
    public Optional<Director> obtenerDirector(Long id) {
        return directorRepository.findById(id);
    }

    /** Guardar entidad tal cual (compat con tu POST actual). */
    @Transactional
    public Director save(Director director) {
        return directorRepository.save(director);
    }

    /** Borrado estilo “antiguo”: desasigna director en sus pelis y elimina. */
    @Transactional
    public void borrarDirector(Director director) {
        // Evita NPE si no hay películas
        List<Movie> movies = director.getMovies();
        if (movies != null && !movies.isEmpty()) {
            for (Movie m : movies) {
                m.setDirector(null);
            }
            movieRepository.saveAll(movies);
        }
        directorRepository.delete(director);
    }

    /* ===========================
       Métodos nuevos (similar a MovieService)
       =========================== */

    /** Lista con paginación simple (mismo approach que en MovieService). */
    public List<Director> getAll(int page, int size) {
        List<Director> all = getAll();
        if (all.isEmpty()) return List.of();
        int from = Math.max(0, page * size);
        if (from >= all.size()) return List.of();
        int to = Math.min(all.size(), from + size);
        return new ArrayList<>(all.subList(from, to));
    }

    /** Obtiene o lanza 404. */
    public Director getByIdOrThrow(Long id) {
        return directorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("No existe el director con el ID: " + id));
    }

    /** Versión delete por id (si quieres usarla desde controller). */
    @Transactional
    public void deleteDirector(Long directorId) {
        Director d = directorRepository.findById(directorId)
            .orElseThrow(() -> new EntityNotFoundException("No existe el director con el ID: " + directorId));
        borrarDirector(d); // reutiliza la lógica compat
    }

    /** DTO por id (lo usas en GET /directores/directores/{id}). */
    public Optional<DirectorDTO> findByIdDto(Long id) {
        return directorRepository.findById(id)
            .map(d -> new DirectorDTO(d.getId(), d.getName()));
    }

    /** Update vía DTO (lo usas en PUT). */
    @Transactional
    public DirectorDTO updateDto(Long id, DirectorDTO dto) {
        Director d = directorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Director no encontrado con ID " + id));
        d.setName(dto.getName());
        directorRepository.save(d);
        return new DirectorDTO(d.getId(), d.getName());
    }
}
