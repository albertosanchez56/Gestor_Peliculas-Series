package com.review.service.service;

import com.review.service.Entidades.Review;
import com.review.service.Entidades.Review.ReviewStatus;
import com.review.service.client.MovieClient;
import com.review.service.dto.CreateReviewRequest;
import com.review.service.dto.MovieAggregatesRequest;
import com.review.service.dto.MovieStatsDTO;
import com.review.service.dto.ReviewDTO;
import com.review.service.dto.UpdateReviewRequest;
import com.review.service.exception.ApiException;
import com.review.service.repositorio.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository repo;
    private final MovieClient movieClient;

    public ReviewService(ReviewRepository repo, MovieClient movieClient) {
        this.repo = repo;
        this.movieClient = movieClient;
    }

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

        // ✅ tras crear, recalcular y actualizar en movie-service
        syncMovieAggregates(saved.getMovieId());

        return toDto(saved);
    }

    public List<ReviewDTO> listVisibleByMovie(Long movieId) {
        return repo.findByMovieIdAndStatusOrderByCreatedAtDesc(movieId, ReviewStatus.VISIBLE)
                .stream().map(this::toDto).toList();
    }

    public MovieStatsDTO stats(Long movieId) {
        long count = repo.countByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);
        Double avg = repo.avgRatingByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);
        return new MovieStatsDTO(avg, count);
    }

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
            affectsAggregates = true; // rating afecta a la media
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
                if (newStatus != oldStatus) {
                    affectsAggregates = true; // status afecta a count/avg (VISIBLE/HIDDEN)
                }
                r.setStatus(newStatus);
                changed = true;
            } catch (IllegalArgumentException ex) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Status inválido. Usa VISIBLE o HIDDEN.");
            }
        }

        if (changed) r.setEdited(true);

        Review saved = repo.save(r);

        // ✅ solo sincroniza si realmente afecta a agregados
        if (affectsAggregates) {
            syncMovieAggregates(movieId);
        }

        return toDto(saved);
    }

    public void deleteMy(Long userId, Long reviewId) {
        Review r = repo.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review no encontrada."));

        if (!r.getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes borrar una review que no es tuya.");
        }

        Long movieId = r.getMovieId();
        repo.delete(r);

        // ✅ tras borrar, recalcular y actualizar en movie-service
        syncMovieAggregates(movieId);
    }

    public List<ReviewDTO> myReviews(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDto).toList();
    }

    private void syncMovieAggregates(Long movieId) {
        long countLong = repo.countByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);
        Double avg = repo.avgRatingByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);

        // avg puede ser null si no hay reviews visibles (OK)
        // movie-service espera voteCount como Integer en tu AggregatesRequest
        int count = (countLong > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) countLong;

        var body = new MovieAggregatesRequest(avg, count);

        try {
            movieClient.updateAggregates(movieId, body);
        } catch (Exception ex) {
            // Estrategia: consistencia eventual. No rompemos create/update/delete por un fallo remoto.
            // Si prefieres, aquí podrías loguear el error.
        }
    }

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
}
