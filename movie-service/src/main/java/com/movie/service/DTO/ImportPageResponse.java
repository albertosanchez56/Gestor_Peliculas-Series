package com.movie.service.DTO;

import java.util.List;

/**
 * Respuesta de importación de una sola página de TMDB popular.
 * Usado para el flujo página a página con progreso en la UI.
 */
public record ImportPageResponse(
    int page,
    List<MovieDTO> movies,
    int created,
    int updated,
    int skipped,
    List<String> errors
) {}
