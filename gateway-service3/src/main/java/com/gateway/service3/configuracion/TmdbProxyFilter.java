package com.gateway.service3.configuracion;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Proxy para /tmdb/** que reenvía a movie-service sin pasar por la ruta del Gateway (evita 403).
 */
@Component
public class TmdbProxyFilter implements org.springframework.cloud.gateway.filter.GlobalFilter, Ordered {

    private static final String TMDB_PREFIX = "/tmdb";
    private static final String MOVIE_SERVICE = "MOVIE-SERVICE";

    private final DiscoveryClient discoveryClient;
    private final org.springframework.web.reactive.function.client.WebClient.Builder webClientBuilder;

    public TmdbProxyFilter(DiscoveryClient discoveryClient,
                            org.springframework.web.reactive.function.client.WebClient.Builder webClientBuilder) {
        this.discoveryClient = discoveryClient;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (!path.startsWith(TMDB_PREFIX)) {
            return chain.filter(exchange);
        }

        List<ServiceInstance> instances = discoveryClient.getInstances(MOVIE_SERVICE);
        if (instances == null || instances.isEmpty()) {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE);
            return exchange.getResponse().setComplete();
        }

        ServiceInstance instance = instances.get(0);
        String baseUrl = "http://" + instance.getHost() + ":" + instance.getPort();
        String query = exchange.getRequest().getURI().getRawQuery();
        String targetUri = baseUrl + path + (query != null && !query.isEmpty() ? "?" + query : "");

        HttpMethod method = exchange.getRequest().getMethod();
        if (method == null) method = HttpMethod.GET;

        return webClientBuilder.build()
                .method(method)
                .uri(targetUri)
                .headers(h -> {
                    HttpHeaders incoming = exchange.getRequest().getHeaders();
                    incoming.forEach((name, values) -> {
                        if (!name.equalsIgnoreCase("host") && !name.equalsIgnoreCase("content-length")) {
                            values.forEach(v -> h.add(name, v));
                        }
                    });
                })
                .body(method.equals(HttpMethod.GET) || method.equals(HttpMethod.HEAD)
                        ? org.springframework.web.reactive.function.BodyInserters.fromValue("")
                        : org.springframework.web.reactive.function.BodyInserters.fromDataBuffers(exchange.getRequest().getBody()))
                .exchangeToMono(clientResponse -> {
                    exchange.getResponse().setStatusCode(clientResponse.statusCode());
                    if (clientResponse.headers().asHttpHeaders().getContentType() != null) {
                        exchange.getResponse().getHeaders().setContentType(clientResponse.headers().asHttpHeaders().getContentType());
                    } else {
                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    }
                    clientResponse.headers().asHttpHeaders().forEach((name, values) -> {
                        if (!name.equalsIgnoreCase("transfer-encoding") && !name.equalsIgnoreCase("content-length")) {
                            values.forEach(v -> exchange.getResponse().getHeaders().add(name, v));
                        }
                    });
                    return exchange.getResponse().writeWith(clientResponse.bodyToFlux(DataBuffer.class));
                })
                .then();
    }

    @Override
    public int getOrder() {
        return -1; // Antes que las rutas
    }
}
