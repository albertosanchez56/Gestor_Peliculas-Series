package com.review.service.repositorio;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.review.service.Entidades.Review;
import com.review.service.Entidades.Review.ReviewStatus;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Listado por película (solo visibles)
    List<Review> findByMovieIdAndStatusOrderByCreatedAtDesc(Long movieId, ReviewStatus status);

    // “Mi reseña” por película
    Optional<Review> findByMovieIdAndUserId(Long movieId, Long userId);

    boolean existsByMovieIdAndUserId(Long movieId, Long userId);

    // Stats (solo visibles)
    long countByMovieIdAndStatus(Long movieId, ReviewStatus status);

    @Query("select avg(r.rating) from Review r where r.movieId = :movieId and r.status = :status")
    Double avgRatingByMovieIdAndStatus(@Param("movieId") Long movieId,
                                       @Param("status") ReviewStatus status);
    
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

}
