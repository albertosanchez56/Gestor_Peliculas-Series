package com.movie.service.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.movie.service.Entidades.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>{

	/*@Query("SELECT DISTINCT m FROM Movie m " +
	         "LEFT JOIN FETCH m.genres " +
	         "LEFT JOIN FETCH m.director")
	  List<Movie> findAllWithGenresAndDirector();*/
}
