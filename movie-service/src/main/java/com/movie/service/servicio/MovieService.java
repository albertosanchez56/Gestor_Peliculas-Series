package com.movie.service.servicio;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.movie.service.Entidades.Director;
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
	
	public Optional<Movie> obtenerPelicula(int id) {
		return movieRepository.findById(id);
	}
	
	public Movie save(Movie movie){
		Movie nuevaPelicula = movieRepository.save(movie);
		return nuevaPelicula;
	}
	
	public void borrarPelicula(Movie movie) {
		movieRepository.delete(movie);
	}
	
}
