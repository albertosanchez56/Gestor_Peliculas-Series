package com.movie.service.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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

	boolean existsBySlugIgnoreCase(String slug);

	Optional<Movie> findBySlugIgnoreCase(String slug);

	boolean existsByTmdbId(Long tmdbId);

	@Query(" select distinct m from Movie m\r\n" + "  left join fetch m.director\r\n" + "  left join fetch m.genres\r\n"
			+ "  where m.averageRating is not null\r\n" + "  order by m.averageRating desc, m.voteCount desc")
	List<Movie> findTopRated(Pageable pageable);

	@Query("""
		select distinct m from Movie m
		left join fetch m.director
		left join fetch m.genres
		join m.genres g
		where lower(g.slug) = lower(:slug) and m.averageRating is not null
		""")
	List<Movie> findTopRatedByGenreSlug(@Param("slug") String slug, Pageable pageable);

	Page<Movie> findAll(Pageable pageable);

	// Búsqueda simple por título
	Page<Movie> findByTitleContainingIgnoreCase(String q, Pageable pageable);

	// Búsqueda combinada (título, director, géneros)
	@Query("""
			  select distinct m
			  from Movie m
			  left join m.director d
			  left join m.genres g
			  where lower(m.title) like lower(concat('%', :q, '%'))
			     or lower(d.name) like lower(concat('%', :q, '%'))
			     or lower(g.name) like lower(concat('%', :q, '%'))
			""")
	Page<Movie> searchAllFields(@Param("q") String q, Pageable pageable);

	@Query("select count(distinct m) from Movie m join m.genres g where g.id = :genreId")
	long countByGenreId(@Param("genreId") Long genreId);

	@Query("select m from Movie m join m.genres g where lower(g.slug) = lower(:slug)")
	Page<Movie> findByGenreSlug(@Param("slug") String slug, Pageable pageable);

	/** Próximos estrenos: releaseDate >= hoy, ordenadas por fecha ascendente */
	Page<Movie> findByReleaseDateGreaterThanEqualOrderByReleaseDateAsc(LocalDate fromDate, Pageable pageable);
}
