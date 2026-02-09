package com.movie.service.tmdb;

import java.util.List;

import com.movie.service.Entidades.Movie;

/**
 * Resultado de importar una sola página de TMDB popular (uso interno del servicio).
 */
public record ImportPageResult(
    List<Movie> movies,
    int created,
    int updated,
    int skipped,
    List<String> errors
) {}
