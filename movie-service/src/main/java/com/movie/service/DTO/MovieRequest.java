package com.movie.service.DTO;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MovieRequest(

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede superar los 255 caracteres")
    String title,

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    String description,

    @NotNull(message = "La fecha de estreno es obligatoria")
    LocalDate releaseDate,

    @NotNull(message = "El ID del director es obligatorio")
    Long directorId,

    @NotNull(message = "Debe haber al menos un género")
    List<Long> genreIds

) {}

