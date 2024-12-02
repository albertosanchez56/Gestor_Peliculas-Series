package com.movie.service.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.service.Entidades.Director;
import com.movie.service.Entidades.Movie;
import com.movie.service.servicio.DirectorService;
import com.movie.service.servicio.MovieService;

@RestController
@RequestMapping("/peliculas")
public class MovieController {

	@Autowired
	private MovieService movieService;
	
	@GetMapping
	public ResponseEntity<List<Movie>> listarPeliculas(){
		List<Movie> peliculas = movieService.getAll();
		
		if(peliculas.isEmpty()) {
			ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(peliculas);
	} 
	
	@GetMapping("/{id}")
	public ResponseEntity<Movie> obtenerPelicula(@PathVariable("id") int id){
		Movie movie = movieService.obtenerPelicula(id);
		if(movie == null) {
			ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(movie);
	}
	
	@PostMapping
	public ResponseEntity<Movie> guardarPelicula(@RequestBody Movie movie){
		Movie nuevaPelicula = movieService.save(movie);
		return ResponseEntity.ok(nuevaPelicula);
	}
	
	@Autowired
    private DirectorService directorService;

	@PostMapping("/directores")
    public ResponseEntity<String> guardarDirector(@RequestBody Director director) {
        // Lógica para guardar el director en la base de datos
        // Asumir que tienes un servicio que guarda el director
        directorService.save(director);
        return ResponseEntity.ok("Director guardado exitosamente");
    }
	
}
