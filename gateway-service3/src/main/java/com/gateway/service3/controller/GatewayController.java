package com.gateway.service3.controller;

import java.util.Collections;
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

import org.springframework.beans.factory.annotation.Qualifier;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Mono;


@Controller
public class GatewayController {

	@Autowired
    private WebClient.Builder webClientBuilder;

	@Autowired
	@Qualifier("peliculasCircuitBreaker")
	private CircuitBreaker peliculasCircuitBreaker;

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
	     Mono<List<Director>> directoresMono = webClientBuilder.build()
	         .get()
	         .uri(peliculasServiceUrl + "/mostrardirectores")
	         .retrieve()
	         .bodyToFlux(Director.class)
	         .collectList()
	         .transformDeferred(CircuitBreakerOperator.of(peliculasCircuitBreaker))
	         .onErrorResume(e -> Mono.just(Collections.emptyList()));

	     Mono<List<Genre>> generosMono = webClientBuilder.build()
	         .get()
	         .uri(peliculasServiceUrl + "/mostrargeneros")
	         .retrieve()
	         .bodyToFlux(Genre.class)
	         .collectList()
	         .transformDeferred(CircuitBreakerOperator.of(peliculasCircuitBreaker))
	         .onErrorResume(e -> Mono.just(Collections.emptyList()));

	     return Mono.zip(directoresMono, generosMono)
	         .map(tuple -> {
	             model.addAttribute("title", "Bienvenido a mi portfolio");
	             model.addAttribute("directores", tuple.getT1());
	             model.addAttribute("generos", tuple.getT2());
	             return "guardarpeliculas";
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
