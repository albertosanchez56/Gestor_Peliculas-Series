package com.movie.service.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.movie.service.Entidades.Movie;
import com.movie.service.repositorio.DirectorRepository;
import com.movie.service.repositorio.GenreRepository;
import com.movie.service.repositorio.MovieRepository;

/**
 * Fase 3: tests unitarios de MovieService (listPaged y búsqueda/filtro).
 */
@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private DirectorRepository directorRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private MovieService movieService;

    private static Page<Movie> emptyPage(int page, int size) {
        return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
    }

    @Test
    @DisplayName("listPaged sin q ni genre llama findAll con pageable")
    void listPaged_noQuery_noGenre_callsFindAll() {
        Pageable pageable = PageRequest.of(0, 25);
        Page<Movie> expected = emptyPage(0, 25);
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(expected);

        Page<Movie> result = movieService.listPaged(0, 25, null, null);

        assertEquals(expected, result);
        verify(movieRepository).findAll(any(Pageable.class));
        verify(movieRepository, never()).findByGenreSlug(any(), any());
        verify(movieRepository, never()).searchAllFields(any(), any());
    }

    @Test
    @DisplayName("listPaged con q vacío y sin genre llama findAll")
    void listPaged_blankQuery_noGenre_callsFindAll() {
        Page<Movie> expected = emptyPage(0, 10);
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(expected);

        Page<Movie> result = movieService.listPaged(0, 10, "  ", null);

        assertEquals(expected, result);
        verify(movieRepository).findAll(any(Pageable.class));
        verify(movieRepository, never()).searchAllFields(any(), any());
    }

    @Test
    @DisplayName("listPaged con genre llama findByGenreSlug")
    void listPaged_withGenre_callsFindByGenreSlug() {
        Page<Movie> expected = emptyPage(0, 20);
        when(movieRepository.findByGenreSlug(eq("accion"), any(Pageable.class))).thenReturn(expected);

        Page<Movie> result = movieService.listPaged(0, 20, null, "accion");

        assertEquals(expected, result);
        verify(movieRepository).findByGenreSlug(eq("accion"), any(Pageable.class));
        verify(movieRepository, never()).findAll(any(Pageable.class));
        verify(movieRepository, never()).searchAllFields(any(), any());
    }

    @Test
    @DisplayName("listPaged con genre tiene prioridad sobre q")
    void listPaged_withGenre_ignoresQuery() {
        Page<Movie> expected = emptyPage(0, 25);
        when(movieRepository.findByGenreSlug(eq("drama"), any(Pageable.class))).thenReturn(expected);

        Page<Movie> result = movieService.listPaged(0, 25, "matrix", "drama");

        assertTrue(result.getContent().isEmpty());
        verify(movieRepository).findByGenreSlug(eq("drama"), any(Pageable.class));
        verify(movieRepository, never()).searchAllFields(any(), any());
    }

    @Test
    @DisplayName("listPaged con q y sin genre llama searchAllFields")
    void listPaged_withQuery_noGenre_callsSearchAllFields() {
        Page<Movie> expected = emptyPage(0, 30);
        when(movieRepository.searchAllFields(eq("dune"), any(Pageable.class))).thenReturn(expected);

        Page<Movie> result = movieService.listPaged(0, 30, "dune", null);

        assertEquals(expected, result);
        verify(movieRepository).searchAllFields(eq("dune"), any(Pageable.class));
        verify(movieRepository, never()).findByGenreSlug(any(), any());
        verify(movieRepository, never()).findAll(any(Pageable.class));
    }
}
