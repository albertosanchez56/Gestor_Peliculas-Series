package com.gateway.service3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.gateway.service3.client.MovieServiceClient;

@Controller
public class GatewayController {

	/*private final MovieServiceClient movieServiceClient;
	
	public GatewayController(MovieServiceClient movieServiceClient) {
        this.movieServiceClient = movieServiceClient;
    }*/
	
	 @GetMapping("/Home")
	    public String showIndex(Model model) {
	        // Agregar cualquier dato que necesites pasar a la vista
	        model.addAttribute("title", "Bienvenido a mi portfolio");
	        
	        // Devolver el nombre de la vista Thymeleaf (index.html)
	        return "index";
	    }
	 
	 @GetMapping("/Pelicula")
	    public String showPelicula(Model model) {
	        // Agregar cualquier dato que necesites pasar a la vista
	        model.addAttribute("title", "Bienvenido a mi portfolio");
	        
	        
	        return "peliculasinfo";
	    }
	 
	 @GetMapping("/movies/add")
	    public String savePelicula(Model model) {
	        // Agregar cualquier dato que necesites pasar a la vista
	        model.addAttribute("title", "Bienvenido a mi portfolio");
	        
	        // Devolver el nombre de la vista Thymeleaf (index.html)
	        return "guardarpeliculas";
	    }
	 @GetMapping("/AgregarDirectores")
	    public String saveDirector(Model model) {
	        // Agregar cualquier dato que necesites pasar a la vista
	        model.addAttribute("title", "Bienvenido a mi portfolio");
	        
	        // Devolver el nombre de la vista Thymeleaf (index.html)
	        return "guardardirector";
	    }
	 
	/* @PostMapping("/movies/add")
	    public String addDirector(String title) {
	        // Usar Feign Client para enviar el dato al microservicio
	        movieServiceClient.addDirector(new MovieServiceClient.DirectorRequest(title));
	        return "redirect:/movies"; // Redirige a la lista de películas
	    }*/
	 
	 
}
