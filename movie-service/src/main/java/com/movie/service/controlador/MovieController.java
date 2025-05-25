package com.movie.service.controlador;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.movie.service.DTO.DirectorDTO;
import com.movie.service.DTO.GenreDTO;
import com.movie.service.DTO.MovieDTO;
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
	
	/*@GetMapping
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
	}*/
	@PostMapping("/guardarpeliculas")
	public ResponseEntity<Map<String, String>> guardarPeliculas(@RequestBody Movie movie) {
	    // Guardar el director en la base de datos
		movieService.save(movie);

	    // Crear un mensaje de respuesta en formato JSON
	    Map<String, String> response = new HashMap<>();
	    response.put("mensaje", "Pelicula guardado exitosamente");

	    return ResponseEntity.ok(response);
	}

	
	/*@GetMapping("/mostrarpeliculas")
	public ResponseEntity<List<Movie>> listarPeliculas(){
		List<Movie> peliculas = movieService.getAll();
		
		if(peliculas.isEmpty()) {
			ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(peliculas);
	} */
	
	 @GetMapping("/mostrarpeliculas")
	    public ResponseEntity<List<MovieDTO>> listarPeliculas() {
	        // Asumimos que getAllWithGenresAndDirector() hace un FETCH JOIN de géneros y director
	        List<Movie> peliculas = movieService.getAll();

	        if (peliculas.isEmpty()) {
	            return ResponseEntity.noContent().build();
	        }

	        List<MovieDTO> dtos = peliculas.stream().map(m -> {
	            // Mapeo del director
	            Director dir = m.getDirector();
	            DirectorDTO dirDto = new DirectorDTO(dir.getId(), dir.getName());

	            // Mapeo de géneros
	            List<GenreDTO> genreDtos = m.getGenres().stream()
	                .map(g -> new GenreDTO(g.getId(), g.getName()))
	                .collect(Collectors.toList());

	            // Construcción del MovieDTO
	            return new MovieDTO(
	                m.getId(),
	                m.getTitle(),
	                m.getDescription(),
	                m.getReleaseDate(),
	                dirDto,
	                genreDtos
	            );
	        }).collect(Collectors.toList());

	        return ResponseEntity.ok(dtos);
	    }
	
	@GetMapping("/peliculas/{id}")
	public ResponseEntity<Movie> obtenerPeliculaPorId(@PathVariable int id) {
		Movie pelicula = movieService.obtenerPelicula(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "No existe la pelicula con el ID: " + id));
	    return ResponseEntity.ok(pelicula);
	}
	
	@PutMapping("/peliculas/{id}")
	public ResponseEntity<Movie> actualizarPelicula(@PathVariable int id, @RequestBody Movie detallesPelicula) {
		Movie movie = movieService.obtenerPelicula(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "No existe la pelicula con el ID: " + id));
	    
		movie.setTitle(detallesPelicula.getTitle());
	    movie.setDescription(detallesPelicula.getDescription());
	    movie.setReleaseDate(detallesPelicula.getReleaseDate());
	    movie.setDirector(detallesPelicula.getDirector());
	    movie.setGenres(detallesPelicula.getGenres());
	    
		Movie peliculaActualizado = movieService.save(movie);
	    
	    return ResponseEntity.ok(peliculaActualizado);
	}
	
	@DeleteMapping("/peliculas/{id}")
	public ResponseEntity<Map<String,Boolean>> borrarPelicula(@PathVariable int id) {
		Movie movie = movieService.obtenerPelicula(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "No existe la pelicula con el ID: " + id));
	    
		movieService.borrarPelicula(movie);
	    
	    
		//Movie peliculaActualizado = movieService.save(movie);
	    Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("eliminar",Boolean.TRUE);
	    
	    return ResponseEntity.ok(respuesta);
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

	
	/*@GetMapping("/mostrardirectores")
	public ResponseEntity<List<Director>> listarDirectores(){
		List<Director> directores = directorService.getAll();
		
		if(directores.isEmpty()) {
			ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(directores);
	} */
	
	@GetMapping("/mostrardirectores")
    public ResponseEntity<List<DirectorDTO>> listarDirectores() {
        // Asegúrate de que este método usa JOIN FETCH para traer también las películas y sus géneros
        List<Director> directores = directorService.getAll();

        if (directores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<DirectorDTO> dtos = directores.stream().map(d -> {
            // Mapear cada película a MovieDTO
            List<MovieDTO> movies = d.getMovies().stream().map(m -> {
                // Mapear géneros de la película
                List<GenreDTO> genres = m.getGenres().stream()
                    .map(g -> new GenreDTO(g.getId(), g.getName()))
                    .toList();

                return new MovieDTO(
                    m.getId(),
                    m.getTitle(),
                    m.getDescription(),
                    m.getReleaseDate(),
                    /* opcional: podrías omitir el director aquí ya que estamos dentro de él */
                    null,
                    genres
                );
            }).toList();

            // Construir el DirectorDTO
            return new DirectorDTO(
                d.getId(),
                d.getName(),
                movies
            );
        }).toList();

        return ResponseEntity.ok(dtos);
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
	    
	    
	   // Director directorActualizado = directorService.save(director);
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
	
	/*@GetMapping("/mostrargeneros")
	public ResponseEntity<List<Genre>> listarGeneros(){
		List<Genre> generos = genreService.getAll();
		
		if(generos.isEmpty()) {
			ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(generos);
	}*/
	
	 @GetMapping("/mostrargeneros")
	    public ResponseEntity<List<GenreDTO>> listarGeneros() {
	        // Usamos un método que fetch-ea también las películas y sus directores
	        List<Genre> generos = genreService.getAll();

	        if (generos.isEmpty()) {
	            return ResponseEntity.noContent().build();
	        }

	        List<GenreDTO> dtos = generos.stream().map(g -> {
	            // Mapear cada Movie a un MovieDTO sin lista de géneros para evitar ciclos
	            List<MovieDTO> movies = g.getMovies().stream().map(m -> {
	                Director d = m.getDirector();
	                DirectorDTO dirDto = new DirectorDTO(d.getId(), d.getName());
	                return new MovieDTO(
	                    m.getId(),
	                    m.getTitle(),
	                    m.getDescription(),
	                    m.getReleaseDate(),
	                    dirDto,
	                    /* omitimos genres aquí */ Collections.emptyList()
	                );
	            }).toList();

	            return new GenreDTO(
	                g.getId(),
	                g.getName(),
	                movies
	            );
	        }).toList();

	        return ResponseEntity.ok(dtos);
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
