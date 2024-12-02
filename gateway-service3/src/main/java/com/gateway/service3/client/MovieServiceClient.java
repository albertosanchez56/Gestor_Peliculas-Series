package com.gateway.service3.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(name = "movie-service", path = "/movies")
public interface MovieServiceClient {

    @PostMapping("/directors")
    void addDirector(@RequestBody DirectorRequest directorRequest);

    class DirectorRequest {
        private String name;

        public DirectorRequest(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}