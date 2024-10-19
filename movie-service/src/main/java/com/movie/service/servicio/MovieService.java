package com.movie.service.servicio;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.movie.service.Entidades.Movie;
import com.movie.service.repositorio.MovieRepository;

@Service
public class MovieService {

	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MovieRepository movieRepository;
	
	public List<Movie> getAll(){
		return movieRepository.findAll();
	}
	
	public Movie obtenerPelicula(int id) {
		return movieRepository.findById(id).orElse(null);
	}
	
	public Movie save(Movie movie){
		Movie nuevaPelicula = movieRepository.save(movie);
		return nuevaPelicula;
	}
}
