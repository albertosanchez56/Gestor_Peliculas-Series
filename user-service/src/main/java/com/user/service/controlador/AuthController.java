package com.user.service.controlador;


import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.user.service.dto.AuthResponseDTO;
import com.user.service.dto.LoginRequestDTO;
import com.user.service.dto.RegisterRequestDTO;
import com.user.service.dto.RegisterResponseDTO;
import com.user.service.dto.UserInfoDTO;
import com.user.service.exception.ApiException;
import com.user.service.repositorio.UserRepository;
import com.user.service.servicio.AuthService;

@RestController
@RequestMapping("/usuario/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
		this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO req) {
        return authService.login(req);
    }
    
    @GetMapping("/me")
    public UserInfoDTO me(Authentication auth) {
        // auth.getName() = username (según el JwtAuthFilter)
        String username = auth.getName();

        var user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Not authenticated"));

        return new UserInfoDTO(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
    }

}