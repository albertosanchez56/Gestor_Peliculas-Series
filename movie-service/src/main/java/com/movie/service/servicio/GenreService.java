package com.movie.service.servicio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movie.service.Entidades.Movie;

import com.movie.service.DTO.GenreCardDTO;
import com.movie.service.Entidades.Genre;
import com.movie.service.repositorio.GenreRepository;
import com.movie.service.repositorio.MovieRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreService {

  private final GenreRepository genreRepository;
  private final MovieRepository movieRepository;

  /* ==== Métodos “antiguos” de compatibilidad ==== */

  public List<Genre> getAll() {
    return genreRepository.findAll();
  }

  public Optional<Genre> obtenerGenero(Long id) {
    return genreRepository.findById(id);
  }

  @Transactional
  public Genre save(Genre g) {
      if (g.getName() == null || g.getName().isBlank()) {
          throw new IllegalArgumentException("El nombre del género es obligatorio");
      }
      // Si no viene slug (o viene vacío), lo generamos a partir del nombre.
      if (g.getSlug() == null || g.getSlug().isBlank()) {
          g.setSlug(generateUniqueSlug(g.getName()));
      } else {
          // Si viene un slug “a mano”, normaliza y asegura unicidad igualmente.
          g.setSlug(generateUniqueSlug(g.getSlug()));
      }
      return genreRepository.save(g);
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

  /** Lista de géneros con conteo y póster (sin repetir imagen entre géneros: se usa la 1ª, 2ª, etc. mejor valorada según disponibilidad). */
  public List<GenreCardDTO> getGenresWithMovieCount() {
    Sort sort = Sort.by(Sort.Direction.DESC, "averageRating")
        .and(Sort.by(Sort.Direction.DESC, "voteCount"));
    Set<String> usedPosterUrls = new HashSet<>();
    List<GenreCardDTO> result = new ArrayList<>();
    int topPerGenre = 15; // suficientes para elegir una imagen no usada

    for (Genre g : genreRepository.findAll()) {
      long count = movieRepository.countByGenreId(g.getId());
      String slug = g.getSlug() != null ? g.getSlug() : "";
      String posterUrl = null;
      if (count > 0 && slug != null && !slug.isBlank()) {
        List<Movie> top = movieRepository.findTopRatedByGenreSlug(slug, PageRequest.of(0, topPerGenre, sort));
        for (Movie m : top) {
          String url = m.getPosterUrl() != null && !m.getPosterUrl().isBlank()
              ? m.getPosterUrl()
              : m.getBackdropUrl();
          if (url != null && !url.isBlank() && !usedPosterUrls.contains(url)) {
            posterUrl = url;
            usedPosterUrls.add(url);
            break;
          }
        }
      }
      result.add(new GenreCardDTO(g.getId(), g.getName(), slug, count, posterUrl));
    }
    return result;
  }

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
  public Genre update(Long id, Genre body) {
    Genre g = genreRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Género no encontrado: " + id));

    g.setName(body.getName());
    g.setDescription(body.getDescription());
    g.setColorHex(body.getColorHex());
    g.setIcon(body.getIcon());

    // si gestionas slug aquí:
    if (body.getSlug() != null && !body.getSlug().isBlank() && !body.getSlug().equals(g.getSlug())) {
      g.setSlug(generateUniqueSlug(body.getSlug()));
    } else if (g.getSlug() == null || g.getSlug().isBlank()) {
      g.setSlug(generateUniqueSlug(g.getName()));
    }

    return genreRepository.save(g);
  }


  @Transactional
  public void delete(Long id) {
    Genre g = genreRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Género no encontrado"));

    // 1) Eliminar filas de la tabla de unión
    movieRepository.detachGenreFromMovies(id);

    // 2) Borrar el género
    genreRepository.delete(g);
  }
  /* ---------- helpers ---------- */

  private String generateUniqueSlug(String source) {
      String base = toSlug(source);
      if (base.isBlank()) base = "genero";
      String candidate = base;
      int i = 2;
      while (genreRepository.existsBySlug(candidate)) {
          candidate = base + "-" + i++;
      }
      return candidate;
  }

  private String toSlug(String input) {
      if (input == null) return "";
      // quita acentos y caracteres raros
      String norm = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD)
                       .replaceAll("\\p{M}", "");
      // minúsculas, no alfanum -> '-', colapsa '-' duplicados, recorta
      String slug = norm.toLowerCase()
                        .replaceAll("[^a-z0-9]+", "-")
                        .replaceAll("(^-|-$)", "")
                        .replaceAll("-{2,}", "-");
      return slug;
  }
}
