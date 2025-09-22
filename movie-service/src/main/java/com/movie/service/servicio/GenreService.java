package com.movie.service.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movie.service.Entidades.Genre;
import com.movie.service.repositorio.GenreRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreService {

  private final GenreRepository genreRepository;

  /* ==== Métodos “antiguos” de compatibilidad ==== */

  public List<Genre> getAll() {
    return genreRepository.findAll();
  }

  public Optional<Genre> obtenerGenero(Long id) {
    return genreRepository.findById(id);
  }

  public Genre save(Genre genero) {
    return genreRepository.save(genero);
  }

  // si antes tenías deleteGenre(long id)
  @Transactional
  public void deleteGenre(Long id) {
    delete(id);
  }

  public boolean existsByName(String name) {
    // ajusta el nombre del método según tu repo (existsByName / existsByNameIgnoreCase)
    return genreRepository.existsByName(name);
  }

  /* ==== Métodos nuevos usados por el controller ==== */

  /** Lista con paginación simple (manteniendo tu ruta actual). */
  public List<Genre> getAll(int page, int size) {
    List<Genre> all = genreRepository.findAll();
    if (all.isEmpty()) return List.of();

    int from = Math.max(0, page * size);
    if (from >= all.size()) return List.of();

    int to = Math.min(all.size(), from + size);
    return new ArrayList<>(all.subList(from, to));
  }

  public Genre getByIdOrThrow(Long id) {
    return genreRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("No existe el genero con el ID: " + id));
  }

  @Transactional
  public Genre updateName(Long id, String name) {
    Genre g = getByIdOrThrow(id);
    g.setName(name);
    return genreRepository.save(g);
  }

  @Transactional
  public void delete(Long id) {
    if (!genreRepository.existsById(id)) {
      throw new EntityNotFoundException("No existe el genero con el ID: " + id);
    }
    genreRepository.deleteById(id);
  }
}
