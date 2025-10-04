package com.movie.service.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.movie.service.Entidades.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

	/*
	 * @Query("SELECT DISTINCT m FROM Movie m " + "LEFT JOIN FETCH m.genres " +
	 * "LEFT JOIN FETCH m.director") List<Movie> findAllWithGenresAndDirector();
	 */
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM movie_genres WHERE genre_id = :genreId", nativeQuery = true)
	int detachGenreFromMovies(@Param("genreId") Long genreId);
	
	Optional<Movie> findByTmdbId(Long tmdbId);
	boolean existsByTmdbId(Long tmdbId);
}
