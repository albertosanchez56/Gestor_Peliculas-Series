package com.movie.service.DTO;

/**
 * Género con conteo de películas para tarjetas en home.
 * posterUrl: póster o backdrop de la película mejor valorada del género (para fondo de la tarjeta).
 */
public record GenreCardDTO(Long id, String name, String slug, long movieCount, String posterUrl) {}
