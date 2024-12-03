package com.movie.service.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.service.Entidades.Director;
import com.movie.service.servicio.DirectorService;

@RestController
@RequestMapping("/directores")
public class DirectorController {

	@Autowired
    private DirectorService directorService;
	
	@PostMapping
    public ResponseEntity<String> guardarDirector(@RequestBody Director director) {
        // Lógica para guardar el director en la base de datos
        // Asumir que tienes un servicio que guarda el director
        directorService.save(director);
        return ResponseEntity.ok("Director guardado exitosamente");
    }
}