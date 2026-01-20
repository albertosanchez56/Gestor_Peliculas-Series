package com.review.service.service;

import com.review.service.Entidades.Review;
import com.review.service.Entidades.Review.ReviewStatus;
import com.review.service.dto.CreateReviewRequest;
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

    public ReviewService(ReviewRepository repo) {
        this.repo = repo;
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
        return toDto(saved);
    }

    public List<ReviewDTO> listVisibleByMovie(Long movieId) {
        return repo.findByMovieIdAndStatusOrderByCreatedAtDesc(movieId, ReviewStatus.VISIBLE)
                .stream().map(this::toDto).toList();
    }

    public MovieStatsDTO stats(Long movieId) {
        long count = repo.countByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);
        Double avg = repo.avgRatingByMovieIdAndStatus(movieId, ReviewStatus.VISIBLE);

        // avg puede ser null si no hay reviews
        return new MovieStatsDTO(avg, count);
    }

    public ReviewDTO updateMy(Long userId, Long reviewId, UpdateReviewRequest req) {
        Review r = repo.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review no encontrada."));

        if (!r.getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes editar una review que no es tuya.");
        }

        boolean changed = false;

        if (req.getRating() != null) {
            r.setRating(req.getRating());
            changed = true;
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
                r.setStatus(ReviewStatus.valueOf(req.getStatus().toUpperCase()));
                changed = true;
            } catch (IllegalArgumentException ex) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Status inválido. Usa VISIBLE o HIDDEN.");
            }
        }

        if (changed) r.setEdited(true);

        Review saved = repo.save(r);
        return toDto(saved);
    }

    public void deleteMy(Long userId, Long reviewId) {
        Review r = repo.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review no encontrada."));

        if (!r.getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes borrar una review que no es tuya.");
        }

        repo.delete(r);
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
    
    public List<ReviewDTO> myReviews(Long userId){
    	   return repo.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    	}

}
