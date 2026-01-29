package com.review.service.service;

import com.review.service.Entidades.Review;
import com.review.service.Entidades.Review.ReviewStatus;
import com.review.service.client.MovieClient;
import com.review.service.dto.*;
import com.review.service.exception.ApiException;
import com.review.service.repositorio.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository repo;
    private final MovieClient movieClient;
    private final RemoteResolverService remoteResolverService;

    public ReviewService(
            ReviewRepository repo,
            MovieClient movieClient,
            RemoteResolverService remoteResolverService
    ) {
        this.repo = repo;
        this.movieClient = movieClient;
        this.remoteResolverService = remoteResolverService;
    }

    /* =========================
       CREATE
       ========================= */

    public ReviewDTO create(Long userId, CreateReviewRequest req) {
        if (repo.existsByMovieIdAndUserId(req.getMovieId(), userId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya has reseñado esta película.");
        }

        Review r = new Review();
        r.setMovieId(req.getMovieId());
        r.setUserId(userId);
        r.setRating(req.getRating());
        r.setComment(req.getComment());
        r.setContainsSpoilers(req.isContainsSpoilers());
        r.setStatus(ReviewStatus.VISIBLE);

        Review saved = repo.save(r);

        // ✅ recalcular y sincronizar agregados en movie-service
        syncMovieAggregates(saved.getMovieId());

        return toDto(saved);
    }

    /* =========================
       LISTADOS
       ========================= */

    /** Público: lista visible (DTO simple) */
    public List<ReviewDTO> listVisibleByMovie(Long movieId) {
        return repo.findByMovieIdAndStatusOrderByCreatedAtDesc(movieId, ReviewStatus.VISIBLE)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /** Público: lista visible con displayName (DTO de vista para el front) */
    public List<ReviewViewDTO> listVisibleByMovieView(Long movieId) {
        return repo.findByMovieIdAndStatusOrderByCreatedAtDesc(movieId, ReviewStatus.VISIBLE)
                .stream()
                .map(this::toViewDto)
                .toList();
    }

    /** Privado: mis reviews (lista) */
    public List<ReviewDTO> myReviews(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /** Privado: mi review de una película (para ocultar formulario en front) */
    public ReviewViewDTO myReviewForMovie(Long userId, Long movieId) {
        Review r = repo.findByMovieIdAndUserId(movieId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No tienes review para esta película."));
        return toViewDto(r);
    }

    /* =========================
       STATS
       ========================= */

    public MovieStatsDTO stats(Long movieId) {
        long count = repo.countByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);
        Double avg = repo.avgRatingByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);
        return new MovieStatsDTO(avg, count);
    }

    /* =========================
       UPDATE
       ========================= */

    public ReviewDTO updateMy(Long userId, Long reviewId, UpdateReviewRequest req) {
        Review r = repo.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review no encontrada."));

        if (!r.getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes editar una review que no es tuya.");
        }

        Long movieId = r.getMovieId();
        ReviewStatus oldStatus = r.getStatus();

        boolean changed = false;
        boolean affectsAggregates = false;

        if (req.getRating() != null) {
            r.setRating(req.getRating());
            changed = true;
            affectsAggregates = true;
        }
        if (req.getComment() != null) {
            r.setComment(req.getComment());
            changed = true;
        }
        if (req.getContainsSpoilers() != null) {
            r.setContainsSpoilers(req.getContainsSpoilers());
            changed = true;
        }
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            try {
                ReviewStatus newStatus = ReviewStatus.valueOf(req.getStatus().toUpperCase());
                if (newStatus != oldStatus) affectsAggregates = true;
                r.setStatus(newStatus);
                changed = true;
            } catch (IllegalArgumentException ex) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Status inválido. Usa VISIBLE o HIDDEN.");
            }
        }

        if (changed) r.setEdited(true);

        Review saved = repo.save(r);

        if (affectsAggregates) {
            syncMovieAggregates(movieId);
        }

        return toDto(saved);
    }

    /* =========================
       DELETE
       ========================= */

    public void deleteMy(Long userId, Long reviewId) {
        Review r = repo.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review no encontrada."));

        if (!r.getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes borrar una review que no es tuya.");
        }

        Long movieId = r.getMovieId();
        repo.delete(r);

        syncMovieAggregates(movieId);
    }

    /* =========================
       SYNC AGGREGATES
       ========================= */

    private void syncMovieAggregates(Long movieId) {
        long countLong = repo.countByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);
        Double avg = repo.avgRatingByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);

        int count = (countLong > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) countLong;

        MovieAggregatesRequest body = new MovieAggregatesRequest(avg, count);

        try {
            movieClient.updateAggregates(movieId, body);
        } catch (Exception ex) {
            // consistencia eventual: no rompemos la operación por fallo remoto
            // aquí podrías loguear si quieres
        }
    }

    /* =========================
       MAPPERS
       ========================= */

    private ReviewDTO toDto(Review r) {
        return new ReviewDTO(
                r.getId(),
                r.getMovieId(),
                r.getUserId(),
                r.getRating(),
                r.getComment(),
                r.isContainsSpoilers(),
                r.getStatus().name(),
                r.isEdited(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }

    private ReviewViewDTO toViewDto(Review r) {
        // ✅ devuelve UserPublicDTO (no Object)
        UserPublicDTO user = remoteResolverService.resolveUser(r.getUserId());
        String displayName = (user == null || user.displayName() == null || user.displayName().isBlank())
                ? "Usuario"
                : user.displayName();

        return new ReviewViewDTO(
                r.getId(),
                r.getMovieId(),
                r.getUserId(),
                displayName,
                r.getRating(),
                r.getComment(),
                r.isContainsSpoilers(),
                r.isEdited(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}
