package com.movie.service.controlador.internal;

import com.movie.service.DTO.MovieAggregatesRequest;
import com.movie.service.DTO.MoviePublicDTO;
import com.movie.service.Entidades.Movie;
import com.movie.service.servicio.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/peliculas/internal/movies")
@RequiredArgsConstructor
public class InternalMovieController {

    private final MovieService movieService;

    // GET /peliculas/internal/movies/{id}
    @GetMapping("/{id}")
    public MoviePublicDTO getPublic(@PathVariable Long id) {
        Movie m = movieService.getByIdOrThrow(id);

        return new MoviePublicDTO(
                m.getId(),
                m.getTitle(),
                m.getPosterUrl(),
                m.getReleaseDate(),
                m.getTmdbId(),
                m.getAverageRating(),
                m.getVoteCount()
        );
    }

    // PATCH /peliculas/internal/movies/{id}/aggregates
    @PatchMapping("/{id}/aggregates")
    public ResponseEntity<Void> updateAggregates(@PathVariable Long id,
                                                 @Valid @RequestBody MovieAggregatesRequest req) {
        movieService.updateAggregates(id, req.averageRating(), req.voteCount());
        return ResponseEntity.noContent().build();
    }
}
