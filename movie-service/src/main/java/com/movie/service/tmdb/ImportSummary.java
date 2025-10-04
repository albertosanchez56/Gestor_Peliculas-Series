// src/main/java/com/movie/service/tmdb/ImportSummary.java
package com.movie.service.tmdb;

import java.util.List;

public record ImportSummary(
    int requested,            // cuántas películas se intentaron importar
    int created,              // cuántas nuevas
    int updated,              // cuántas existentes actualizadas
    int skipped,              // cuántas se saltaron (errores o duplicados)
    List<Long> importedIds,   // IDs internos creados/actualizados
    List<String> errors       // mensajes de error si los hubo
) {}
