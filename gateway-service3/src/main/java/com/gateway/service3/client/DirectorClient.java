package com.gateway.service3.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gateway.service3.modelos.Director;

//@FeignClient(name = "movie-service")
public interface DirectorClient {

	
	@PostMapping("/peliculas/directores")
	public Director crearDirector(@RequestBody Director director);
}
