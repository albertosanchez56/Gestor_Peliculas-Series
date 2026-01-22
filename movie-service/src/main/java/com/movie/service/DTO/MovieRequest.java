package com.movie.service.DTO;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

public record MovieRequest(

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede superar los 255 caracteres")
    String title,

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    String description,

    @NotNull(message = "La fecha de estreno es obligatoria")
    @PastOrPresent(message = "La fecha de estreno no puede estar en el futuro")
    LocalDate releaseDate,

    @NotNull(message = "El ID del director es obligatorio")
    @Positive(message = "El ID del director debe ser positivo")
    Long directorId,

    //Solo UNA vez @Positive en el ELEMENTO
    @NotNull(message = "Debe enviar la lista de géneros")
    @Size(min = 1, message = "Debe haber al menos un género")
    List<Long> genreIds,

    // ---- opcionales ----
    @Min(value = 1,  message = "La duración mínima es 1 minuto")
    @Max(value = 600, message = "La duración máxima razonable es 600 minutos")
    Integer durationMinutes,

    @Pattern(regexp = "^[a-zA-Z]{2}$", message = "Idioma original debe tener 2 letras (ISO-639-1)")
    @Size(max = 10, message = "Idioma original demasiado largo")
    String originalLanguage,

    @URL(message = "El póster debe ser una URL válida")
    @Size(max = 2048, message = "URL del póster demasiado larga")
    String posterUrl,

    @URL(message = "El backdrop debe ser una URL válida")
    @Size(max = 2048, message = "URL del backdrop demasiado larga")
    String backdropUrl,

    @URL(message = "El tráiler debe ser una URL válida")
    @Size(max = 2048, message = "URL del tráiler demasiado larga")
    String trailerUrl,

    @Pattern(regexp = "^(TP|7|12|16|18|PG|PG-13|R)$",
             message = "Clasificación no válida (usa TP, 7, 12, 16, 18, PG, PG-13 o R)")
    String ageRating
) {}
