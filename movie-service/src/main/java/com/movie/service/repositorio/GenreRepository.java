package com.movie.service.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.movie.service.Entidades.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long>{
	
	/*@Query("SELECT DISTINCT g FROM Genre g " +
	         "LEFT JOIN FETCH g.movies m " +
	         "LEFT JOIN FETCH m.director")
	  List<Genre> findAllWithMoviesAndDirectors();*/

	boolean existsByName(String name);
}
