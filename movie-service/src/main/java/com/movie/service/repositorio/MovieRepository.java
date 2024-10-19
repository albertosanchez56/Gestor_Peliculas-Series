package com.movie.service.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.service.Entidades.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer>{

}
