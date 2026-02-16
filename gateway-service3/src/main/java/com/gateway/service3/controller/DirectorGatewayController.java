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

import com.gateway.service3.modelos.Director;

import org.springframework.beans.factory.annotation.Qualifier;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/directores")
public class DirectorGatewayController {

	@Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    @Qualifier("peliculasCircuitBreaker")
    private CircuitBreaker peliculasCircuitBreaker;

    private final String peliculasServiceUrl = "http://localhost:9090/peliculas/directores";

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
            .transformDeferred(CircuitBreakerOperator.of(peliculasCircuitBreaker))
            .then(Mono.defer(() -> {
                exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                exchange.getResponse().getHeaders().setLocation(URI.create("/AgregarDirectores"));
                return exchange.getResponse().setComplete();
            }))
            .onErrorResume(e -> {
                exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                return exchange.getResponse().setComplete();
            });
    }
}
