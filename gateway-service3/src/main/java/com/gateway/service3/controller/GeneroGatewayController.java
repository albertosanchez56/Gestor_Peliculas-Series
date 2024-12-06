package com.gateway.service3.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.gateway.service3.modelos.Genre;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/generos")
public class GeneroGatewayController {

	@Autowired
    private WebClient.Builder webClientBuilder;

    private final String peliculasServiceUrl = "http://localhost:9090/peliculas/generos";
    
    public GeneroGatewayController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    
    @PostMapping("/guardar-genero")
    public Mono<Void> guardarGenero(@ModelAttribute Genre genero, ServerWebExchange exchange) {
        return webClientBuilder.build()
            .post()
            .uri(peliculasServiceUrl)
            .bodyValue(genero)
            .retrieve()
            .bodyToMono(String.class)
            .then(Mono.defer(() -> {
                exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                exchange.getResponse().getHeaders().setLocation(URI.create("/AgregarGeneros"));
                return exchange.getResponse().setComplete();
            }));
    }
}
