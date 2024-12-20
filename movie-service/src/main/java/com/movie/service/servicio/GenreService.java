package com.movie.service.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.movie.service.Entidades.Genre;
import com.movie.service.repositorio.GenreRepository;

@Service
public class GenreService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private GenreRepository genreRepository;
	
	public List<Genre> getAll(){
		return genreRepository.findAll();
	}
	
	public  Optional<Genre> obtenerGenero(int id) {
		return genreRepository.findById(id);
	}
	
	
	public Genre save(Genre genero){
		Genre nuevoGenero = genreRepository.save(genero);
		return nuevoGenero;
	}
	
	public void borrarGenero(Genre genero) {
        genreRepository.delete(genero);
    }
	
	public boolean existsByName(String name) {
        return genreRepository.existsByName(name);
    }
}
