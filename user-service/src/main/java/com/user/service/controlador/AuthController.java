package com.user.service.controlador;


import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.user.service.dto.LoginRequestDTO;
import com.user.service.dto.LoginResponseDTO;
import com.user.service.dto.RegisterRequestDTO;
import com.user.service.dto.RegisterResponseDTO;
import com.user.service.servicio.AuthService;

@RestController
@RequestMapping("/usuario/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO req) {
        return authService.login(req);
    }
}