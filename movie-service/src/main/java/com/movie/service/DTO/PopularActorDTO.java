package com.movie.service.DTO;

/**
 * Resumen de un actor para el carrusel "Actores más populares" en el home.
 */
public record PopularActorDTO(
    Long tmdbPersonId,
    String personName,
    String profileUrl,
    long movieCount,
    String mostPopularMovieTitle,
    String mostPopularCharacterName
) {}
