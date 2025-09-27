package com.movie.service.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.service.Entidades.Director;

public interface DirectorRepository extends JpaRepository<Director, Long> {
	
	Optional<Director> findByTmdbId(Long tmdbId);
	boolean existsByNameIgnoreCase(String name);
	
}