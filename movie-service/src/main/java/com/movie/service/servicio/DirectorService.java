package com.movie.service.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.movie.service.Entidades.Director;
import com.movie.service.repositorio.DirectorRepository;

@Service
public class DirectorService {

	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private DirectorRepository directorRepository;
	
	public List<Director> getAll(){
		return directorRepository.findAll();
	}
	
	/*public Director obtenerDirector(int id) {
		return directorRepository.findById(id).orElse(null);
	}*/
	
	public Optional<Director> obtenerDirector(int id) {
	    return directorRepository.findById(id);
	}
	
	public Director save(Director director){
		Director nuevoDirector = directorRepository.save(director);
		return nuevoDirector;
	}
	
	public void borrarDirector(Director director) {
        directorRepository.delete(director);
    }
}
