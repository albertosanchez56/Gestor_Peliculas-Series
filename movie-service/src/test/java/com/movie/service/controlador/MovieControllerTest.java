package com.movie.service.controlador;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.movie.service.Entidades.Movie;
import com.movie.service.repositorio.CastCreditRepository;
import com.movie.service.servicio.MovieService;
import com.movie.service.tmdb.CastImportService;

/**
 * Fase 3: tests del MovieController (GET /peliculas/peliculas con page, size, q, genre).
 * addFilters = false para no aplicar Spring Security y poder probar el controller aislado.
 */
@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private CastCreditRepository castCreditRepository;

    @MockBean
    private CastImportService castImportService;

    private static Page<Movie> emptyPage() {
        return new PageImpl<>(List.of(), PageRequest.of(0, 25), 0);
    }

    @Test
    @DisplayName("GET /peliculas/peliculas con page y size devuelve 200 y llama listPaged")
    void listPaged_withPageAndSize_returns200() throws Exception {
        when(movieService.listPaged(0, 25, null, null)).thenReturn(emptyPage());

        mockMvc.perform(get("/peliculas/peliculas")
                .param("page", "0")
                .param("size", "25")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(movieService).listPaged(0, 25, null, null);
    }

    @Test
    @DisplayName("GET /peliculas/peliculas con q y genre devuelve 200 y llama listPaged con ambos")
    void listPaged_withQueryAndGenre_returns200() throws Exception {
        when(movieService.listPaged(1, 30, "matrix", "accion")).thenReturn(emptyPage());

        mockMvc.perform(get("/peliculas/peliculas")
                .param("page", "1")
                .param("size", "30")
                .param("q", "matrix")
                .param("genre", "accion")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(movieService).listPaged(1, 30, "matrix", "accion");
    }

    @Test
    @DisplayName("GET /peliculas/peliculas sin params usa defaults page=0 size=25")
    void listPaged_noParams_usesDefaults() throws Exception {
        when(movieService.listPaged(0, 25, null, null)).thenReturn(emptyPage());

        mockMvc.perform(get("/peliculas/peliculas").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(movieService).listPaged(eq(0), eq(25), eq(null), eq(null));
    }
}
