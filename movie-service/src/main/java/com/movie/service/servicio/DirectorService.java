package com.movie.service.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.movie.service.DTO.DirectorDTO;
import com.movie.service.Entidades.Director;
import com.movie.service.Entidades.Movie;
import com.movie.service.repositorio.DirectorRepository;
import com.movie.service.repositorio.MovieRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DirectorService {

	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private DirectorRepository directorRepository;
	
	@Autowired
	private MovieRepository movieRepository;
	
	public List<Director> getAll(){
		return directorRepository.findAll();
	}
	
	/*public Director obtenerDirector(int id) {
		return directorRepository.findById(id).orElse(null);
	}*/
	
	public Optional<Director> obtenerDirector(long id) {
	    return directorRepository.findById(id);
	}
	
	public Director save(Director director){
		Director nuevoDirector = directorRepository.save(director);
		return nuevoDirector;
	}
	
	public void borrarDirector(Director director) {
        directorRepository.delete(director);
    }
	
	@Transactional
	public void deleteDirector(Long directorId) 
	{
		Director director = directorRepository.findById(directorId).orElseThrow(() ->
        new EntityNotFoundException("Director no encontrado con ID " + directorId));
		
		for(Movie m : director.getMovies()) 
		{
			m.setDirector(null);
		}
		
		movieRepository.saveAll(director.getMovies());
		
		directorRepository.delete(director);
	}
	
	public Optional<DirectorDTO> findByIdDto(Long id) {
	    return directorRepository.findById(id)
	       .map(d -> new DirectorDTO(d.getId(), d.getName()));
	  }

	  @Transactional
	  public DirectorDTO updateDto(Long id, DirectorDTO dto) {
	    Director d = directorRepository.findById(id)
	      .orElseThrow(() -> new EntityNotFoundException("Director no encontrado"));
	    d.setName(dto.getName());
	    directorRepository.save(d);
	    return new DirectorDTO(d.getId(), d.getName());
	  }
}
