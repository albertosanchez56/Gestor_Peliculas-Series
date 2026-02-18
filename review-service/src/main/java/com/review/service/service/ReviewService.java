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

    public ReviewService(ReviewRepository repo,
                         MovieClient movieClient,
                         RemoteResolverService remoteResolverService) {
        this.repo = repo;
        this.movieClient = movieClient;
        this.remoteResolverService = remoteResolverService;
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

        syncMovieAggregates(saved.getMovieId());

        return toDto(saved);
    }

    // ✅ NUEVO: listado "enriquecido" con displayName
    public List<ReviewViewDTO> listVisibleByMovieView(Long movieId) {
        return repo.findByMovieIdAndStatusOrderByCreatedAtDesc(movieId, ReviewStatus.VISIBLE)
                .stream()
                .map(this::toViewDto)
                .toList();
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

    public void deleteMy(Long userId, Long reviewId) {
        delete(userId, reviewId, false);
    }

    /** Borra una review: solo el dueño puede, o un admin puede borrar cualquiera. */
    public void delete(Long userId, Long reviewId, boolean isAdmin) {
        Review r = repo.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review no encontrada."));

        if (!isAdmin && !r.getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes borrar una review que no es tuya.");
        }

        Long movieId = r.getMovieId();
        repo.delete(r);
        syncMovieAggregates(movieId);
    }

    public List<ReviewDTO> myReviews(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDto).toList();
    }

    private void syncMovieAggregates(Long movieId) {
        long countLong = repo.countByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);
        Double avg = repo.avgRatingByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);
        int count = (countLong > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) countLong;

        var body = new MovieAggregatesRequest(avg, count);

        try {
            movieClient.updateAggregates(movieId, body);
        } catch (Exception ignored) {
        }
    }
    
    public ReviewViewDTO myReviewForMovie(Long userId, Long movieId) {
        return repo.findByMovieIdAndUserId(movieId, userId)
                .map(this::toViewDto)   // usa tu método que mete displayName
                .orElse(null);
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

    private ReviewViewDTO toViewDto(Review r) {
        UserPublicDTO user = remoteResolverService.resolveUser(r.getUserId());
        String display = (user != null && user.displayName() != null) ? user.displayName() : "Usuario";

        return new ReviewViewDTO(
                r.getId(),
                r.getMovieId(),
                r.getUserId(),
                display,
                r.getRating(),
                r.getComment(),
                r.isContainsSpoilers(),
                r.isEdited(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}
