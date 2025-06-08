package com.movie.service.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.service.Entidades.Director;

public interface DirectorRepository extends JpaRepository<Director, Long> {
	
}