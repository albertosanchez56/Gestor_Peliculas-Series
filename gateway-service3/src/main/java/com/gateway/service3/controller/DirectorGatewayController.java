package com.gateway.service3.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.gateway.service3.client.DirectorClient;
import com.gateway.service3.modelos.Director;

@RestController
@RequestMapping("/directores")
public class DirectorGatewayController {

    /*@Autowired
    private DirectorClient directorClient;*/

    /*@Autowired
    private RestTemplate restTemplate;*/
    

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String peliculasServiceUrl = "http://localhost:9090/peliculas/directores";

    /*@PostMapping("/guardar-director")
    public Mono<String> guardarDirector(@ModelAttribute Director director) {
        return webClientBuilder.build()
            .post()
            .uri(peliculasServiceUrl)
            .bodyValue(director)
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> "redirect:/peliculasinfo");  // Redirigir de manera reactiva
    }*/
    
    public DirectorGatewayController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostMapping("/guardar-director")
    public Mono<Void> guardarDirector(@ModelAttribute Director director, ServerWebExchange exchange) {
        return webClientBuilder.build()
            .post()
            .uri(peliculasServiceUrl)
            .bodyValue(director)
            .retrieve()
            .bodyToMono(String.class)
            .then(Mono.defer(() -> {
                exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                exchange.getResponse().getHeaders().setLocation(URI.create("/AgregarDirectores"));
                return exchange.getResponse().setComplete();
            }));
    }
}
