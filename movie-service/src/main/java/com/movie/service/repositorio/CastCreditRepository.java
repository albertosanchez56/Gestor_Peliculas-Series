// src/main/java/com/movie/service/repositorio/CastCreditRepository.java
package com.movie.service.repositorio;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.movie.service.Entidades.CastCredit;

public interface CastCreditRepository extends JpaRepository<CastCredit, Long> {
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	int deleteByMovieId(Long movieId);

	List<CastCredit> findByMovieIdOrderByOrderIndexAsc(Long movieId);

	/** Actores más populares: una fila por persona, con película más popular y personaje. */
	@Query(nativeQuery = true, value = """
		SELECT agg.tmdb_person_id, agg.person_name, agg.profile_url, agg.movie_count,
		       best.most_popular_movie_title, best.most_popular_character_name
		FROM (
		  SELECT c.tmdb_person_id,
		         MAX(c.person_name) AS person_name,
		         MAX(c.profile_url) AS profile_url,
		         COUNT(DISTINCT c.movie_id) AS movie_count,
		         COALESCE(MAX(c.popularity), -1) AS ord
		  FROM cast_credit c
		  GROUP BY c.tmdb_person_id
		) agg
		LEFT JOIN (
		  SELECT tmdb_person_id, title AS most_popular_movie_title, character_name AS most_popular_character_name
		  FROM (
		    SELECT c.tmdb_person_id, p.title, c.character_name,
		           ROW_NUMBER() OVER (PARTITION BY c.tmdb_person_id ORDER BY COALESCE(p.popularity, -1) DESC) AS rn
		    FROM cast_credit c
		    INNER JOIN pelicula p ON p.id = c.movie_id
		  ) ranked
		  WHERE ranked.rn = 1
		) best ON best.tmdb_person_id = agg.tmdb_person_id
		ORDER BY agg.ord DESC
		""")
	List<Object[]> findPopularActors(Pageable pageable);
}
