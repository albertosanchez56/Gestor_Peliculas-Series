package com.review.service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.review.service.config.UserFeignConfig;
import com.review.service.dto.UserPublicDTO;

@FeignClient(
        name = "user-service",
        configuration = UserFeignConfig.class
)
public interface UserClient {

    @GetMapping("/usuario/internal/users/{id}")
    UserPublicDTO getPublicById(@PathVariable("id") Long id);
}
