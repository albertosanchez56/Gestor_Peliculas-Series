package com.gateway.service3.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;

import com.gateway.service3.client.MovieServiceClient;
import com.gateway.service3.modelos.Director;
import com.gateway.service3.modelos.Genre;

import reactor.core.publisher.Mono;


@Controller
public class GatewayController {
	
	@Autowired
    private WebClient.Builder webClientBuilder;

    private final String peliculasServiceUrl = "http://localhost:9090/peliculas";
	
	 @GetMapping("/Home")
	    public String showIndex(Model model) {
	        model.addAttribute("title", "Bienvenido a mi portfolio");
	        return "index";
	    }
	 
	 @GetMapping("/Pelicula")
	    public String showPelicula(Model model) {
	        model.addAttribute("title", "Bienvenido a mi portfolio");
	        return "peliculasinfo";
	    }
	 
	 @GetMapping("/AgregarPelicula")
	 public Mono<String> savePelicula(Model model) {
	     // Obtener directores
	     Mono<List<Director>> directoresMono = webClientBuilder.build()
	         .get()
	         .uri(peliculasServiceUrl + "/mostrardirectores") // Endpoint para obtener directores
	         .retrieve()
	         .bodyToFlux(Director.class)
	         .collectList();

	     // Obtener géneros
	     Mono<List<Genre>> generosMono = webClientBuilder.build()
	         .get()
	         .uri(peliculasServiceUrl + "/mostrargeneros") // Endpoint para obtener géneros
	         .retrieve()
	         .bodyToFlux(Genre.class)
	         .collectList();

	     // Combinar ambas solicitudes
	     return Mono.zip(directoresMono, generosMono)
	         .map(tuple -> {
	             model.addAttribute("title", "Bienvenido a mi portfolio");
	             model.addAttribute("directores", tuple.getT1());
	             model.addAttribute("generos", tuple.getT2()); // Agregar los géneros al modelo
	             return "guardarpeliculas"; // Nombre de la vista
	         });
	 }

	 
	 @GetMapping("/AgregarDirectores")
	    public String saveDirector(Model model) {
	        model.addAttribute("title", "Bienvenido a mi portfolio");
	        return "guardardirector";
	    }
	 
	 @GetMapping("/AgregarGeneros")
	    public String saveGenero(Model model) {
	        model.addAttribute("title", "Bienvenido a mi portfolio");
	        return "guardargenero";
	    }
	 
	 
}
