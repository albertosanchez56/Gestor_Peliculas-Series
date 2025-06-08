package com.movie.service.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.movie.service.Entidades.Genre;
import com.movie.service.Entidades.Movie;
import com.movie.service.repositorio.GenreRepository;
import com.movie.service.repositorio.MovieRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GenreService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private GenreRepository genreRepository;
	
	@Autowired
	private MovieRepository movieRepository;
	
	public List<Genre> getAll(){
		return genreRepository.findAll();
	}
	
	public  Optional<Genre> obtenerGenero(long id) {
		return genreRepository.findById(id);
	}
	
	
	public Genre save(Genre genero){
		Genre nuevoGenero = genreRepository.save(genero);
		return nuevoGenero;
	}
	
	@Transactional
	public void deleteGenre(Long genreId) {
		Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new EntityNotFoundException("Género con id " + genreId + " no encontrado"));
		
		for(Movie m : genre.getMovies()) 
		{
			m.getGenres().remove(genre);
		}
		
		movieRepository.saveAll(genre.getMovies());
		
		genreRepository.delete(genre);
	}
	
	public void borrarGenero(Genre genero) {
        genreRepository.delete(genero);
    }
	
	public boolean existsByName(String name) {
        return genreRepository.existsByName(name);
    }
}
