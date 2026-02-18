package com.review.service.controller;

import com.review.service.dto.CreateReviewRequest;
import com.review.service.dto.MovieStatsDTO;
import com.review.service.dto.ReviewDTO;
import com.review.service.dto.ReviewViewDTO;
import com.review.service.dto.UpdateReviewRequest;
import com.review.service.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    /* =========================
       PÚBLICO
       ========================= */

    // Público: reviews visibles de una película (con displayName y fechas)
    @GetMapping("/movie/{movieId}")
    public List<ReviewViewDTO> listByMovie(@PathVariable Long movieId) {
        return service.listVisibleByMovieView(movieId);
    }

    // Público: stats de una película
    @GetMapping("/movie/{movieId}/stats")
    public MovieStatsDTO stats(@PathVariable Long movieId) {
        return service.stats(movieId);
    }

    /* =========================
       PRIVADO (JWT)
       ========================= */

    // Privado: crear review
    @PostMapping
    public ResponseEntity<ReviewDTO> create(@Valid @RequestBody CreateReviewRequest req,
                                           Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        ReviewDTO dto = service.create(userId, req);
        return ResponseEntity.ok(dto);
    }

    // Privado: editar mi review
    @PatchMapping("/{reviewId}")
    public ReviewDTO update(@PathVariable Long reviewId,
                            @Valid @RequestBody UpdateReviewRequest req,
                            Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        return service.updateMy(userId, reviewId, req);
    }

    // Privado: borrar mi review (o cualquier review si es ADMIN)
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId,
                                       Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        service.delete(userId, reviewId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    // Privado: mis reviews (lista)
    @GetMapping("/me")
    public List<ReviewDTO> me(Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        return service.myReviews(userId);
    }

    //Privado: mi review de UNA película (para ocultar el formulario en el front)
    @GetMapping("/me/movie/{movieId}")
    public ReviewViewDTO myReviewForMovie(@PathVariable Long movieId,
                                          Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        return service.myReviewForMovie(userId, movieId);
    }
    
    @GetMapping("/movie/{movieId}/view")
    public List<ReviewViewDTO> listByMovieView(@PathVariable Long movieId) {
        return service.listVisibleByMovieView(movieId);
    }
}
