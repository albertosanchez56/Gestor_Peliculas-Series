// src/main/java/com/movie/service/repositorio/CastCreditRepository.java
package com.movie.service.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.movie.service.Entidades.CastCredit;

public interface CastCreditRepository extends JpaRepository<CastCredit, Long> {
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	int deleteByMovieId(Long movieId);

	List<CastCredit> findByMovieIdOrderByOrderIndexAsc(Long movieId);

}
