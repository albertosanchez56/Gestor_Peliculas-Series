package com.gateway.service3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    @PostMapping("/guardar-director")
    public Mono<String> guardarDirector(@ModelAttribute Director director) {
        return webClientBuilder.build()
            .post()
            .uri(peliculasServiceUrl)
            .bodyValue(director)
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> "redirect:/guardardirector");  // Redirigir de manera reactiva
    }
}
