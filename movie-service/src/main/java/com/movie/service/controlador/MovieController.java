package com.movie.service.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.service.Entidades.Director;
import com.movie.service.Entidades.Genre;
import com.movie.service.Entidades.Movie;
import com.movie.service.servicio.DirectorService;
import com.movie.service.servicio.GenreService;
import com.movie.service.servicio.MovieService;

@RestController
@RequestMapping("/peliculas")
@CrossOrigin(origins = "http://localhost:4200")
public class MovieController {

	@Autowired
	private MovieService movieService;
	
	@Autowired
    private DirectorService directorService;
	
	@Autowired
	private GenreService genreService;
	
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
	
	

	@PostMapping("/directores")
    public ResponseEntity<String> guardarDirector(@RequestBody Director director) {
        // Lógica para guardar el director en la base de datos
        // Asumir que tienes un servicio que guarda el director
        directorService.save(director);
        return ResponseEntity.ok("Director guardado exitosamente");
    }
	
	@GetMapping("/mostrardirectores")
	public ResponseEntity<List<Director>> listarDirectores(){
		List<Director> directores = directorService.getAll();
		
		if(directores.isEmpty()) {
			ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(directores);
	} 
	
	@PostMapping("/generos")
    public ResponseEntity<String> guardarGenero(@RequestBody Genre generos) {
        // Lógica para guardar el director en la base de datos
        // Asumir que tienes un servicio que guarda el director
		genreService.save(generos);
        return ResponseEntity.ok("Genero guardado exitosamente");
    }
	
	@GetMapping("/mostrargeneros")
	public ResponseEntity<List<Genre>> listarGeneros(){
		List<Genre> generos = genreService.getAll();
		
		if(generos.isEmpty()) {
			ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(generos);
	}
	
}
