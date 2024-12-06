package com.movie.service.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.service.Entidades.Genre;

public interface GenreRepository extends JpaRepository<Genre, Integer>{

}
