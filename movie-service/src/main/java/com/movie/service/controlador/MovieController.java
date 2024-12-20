package com.movie.service.controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.service.Entidades.Director;
import com.movie.service.Entidades.Genre;
import com.movie.service.Entidades.Movie;
import com.movie.service.servicio.DirectorService;
import com.movie.service.servicio.GenreService;
import com.movie.service.servicio.MovieService;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.HttpStatus; // Para HttpStatus.NOT_FOUND
import org.springframework.web.server.ResponseStatusException; // Para ResponseStatusException


@RestController
@RequestMapping("/peliculas")
//@CrossOrigin(origins = "http://localhost:4200")
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
	
	

	@PostMapping("/guardardirectores")
	public ResponseEntity<Map<String, String>> guardarDirector(@RequestBody Director director) {
	    // Guardar el director en la base de datos
	    directorService.save(director);

	    // Crear un mensaje de respuesta en formato JSON
	    Map<String, String> response = new HashMap<>();
	    response.put("mensaje", "Director guardado exitosamente");

	    return ResponseEntity.ok(response);
	}

	
	@GetMapping("/mostrardirectores")
	public ResponseEntity<List<Director>> listarDirectores(){
		List<Director> directores = directorService.getAll();
		
		if(directores.isEmpty()) {
			ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(directores);
	} 
	
	@GetMapping("/directores/{id}")
	public ResponseEntity<Director> obtenerDirectorPorId(@PathVariable int id) {
	    Director director = directorService.obtenerDirector(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "No existe el director con el ID: " + id));
	    return ResponseEntity.ok(director);
	}
	
	@PutMapping("/directores/{id}")
	public ResponseEntity<Director> actualizarDirector(@PathVariable int id, @RequestBody Director detallesDirector) {
	    Director director = directorService.obtenerDirector(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "No existe el director con el ID: " + id));
	    
	    director.setName(detallesDirector.getName());
	    
	    Director directorActualizado = directorService.save(director);
	    
	    return ResponseEntity.ok(directorActualizado);
	}
	
	@DeleteMapping("/directores/{id}")
	public ResponseEntity<Map<String,Boolean>> borrarDirector(@PathVariable int id) {
	    Director director = directorService.obtenerDirector(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "No existe el director con el ID: " + id));
	    
	    directorService.borrarDirector(director);
	    
	    
	    Director directorActualizado = directorService.save(director);
	    Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("eliminar",Boolean.TRUE);
	    
	    return ResponseEntity.ok(respuesta);
	}
	
	@PostMapping("/guardargeneros")
	public ResponseEntity<Map<String, String>> guardarGenero(@RequestBody Genre genero) {
	    // Guardar el director en la base de datos
	    genreService.save(genero);

	    // Crear un mensaje de respuesta en formato JSON
	    Map<String, String> response = new HashMap<>();
	    response.put("mensaje", "Genero guardado exitosamente");

	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/mostrargeneros")
	public ResponseEntity<List<Genre>> listarGeneros(){
		List<Genre> generos = genreService.getAll();
		
		if(generos.isEmpty()) {
			ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(generos);
	}
	
	@GetMapping("/generos/{id}")
	public ResponseEntity<Genre> obtenerGenerosPorId(@PathVariable int id) {
	    Genre genero = genreService.obtenerGenero(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "No existe el genero con el ID: " + id));
	    return ResponseEntity.ok(genero);
	}
	
	@PutMapping("/generos/{id}")
	public ResponseEntity<Genre> actualizarGenero(@PathVariable int id, @RequestBody Genre detallesGenero) {
	    Genre genero = genreService.obtenerGenero(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "No existe el genero con el ID: " + id));
	    
	    genero.setName(detallesGenero.getName());
	    
	    Genre generoActualizado = genreService.save(genero);
	    
	    return ResponseEntity.ok(generoActualizado);
	}
	
	@DeleteMapping("/generos/{id}")
	public ResponseEntity<Map<String,Boolean>> borrarGenero(@PathVariable int id) {
	    Genre genero = genreService.obtenerGenero(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "No existe el genero con el ID: " + id));
	    
	    genreService.borrarGenero(genero);
	    
	    
	    //Genre generoActualizado = genreService.save(genero);
	    Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("eliminar",Boolean.TRUE);
	    
	    return ResponseEntity.ok(respuesta);
	}
	
	@GetMapping("/generosexists/{name}")
    public ResponseEntity<Boolean> checkIfNameExists(@PathVariable String name) {
        boolean exists = genreService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
	
	
}
