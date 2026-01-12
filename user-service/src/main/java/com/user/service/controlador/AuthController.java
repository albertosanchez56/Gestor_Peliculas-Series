package com.user.service.controlador;


import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.user.service.Entidades.User;
import com.user.service.dto.AuthResponseDTO;
import com.user.service.dto.ChangePasswordRequest;
import com.user.service.dto.LoginRequestDTO;
import com.user.service.dto.RegisterRequestDTO;
import com.user.service.dto.RegisterResponseDTO;
import com.user.service.dto.UpdateMeRequest;
import com.user.service.dto.UserInfoDTO;
import com.user.service.exception.ApiException;
import com.user.service.repositorio.UserRepository;
import com.user.service.servicio.AuthService;
import com.user.service.servicio.UserService;

@RestController
@RequestMapping("/usuario/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    
    private final UserService userService;

    public AuthController(AuthService authService, UserRepository userRepository, UserService userService) {
        this.authService = authService;
		this.userRepository = userRepository;
		this.userService = userService;
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
    
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me")
    public ResponseEntity<UserInfoDTO> updateMe(
            @Valid @RequestBody UpdateMeRequest req,
            Authentication auth
    ) {
        Long userId = Long.parseLong(auth.getName()); // ← viene del JWT (sub)

        User updated = userService.updateMe(
                userId,
                req.getDisplayName(),
                req.getEmail()
        );

        return ResponseEntity.ok(new UserInfoDTO(
                updated.getId(),
                updated.getUsername(),
                updated.getDisplayName(),
                updated.getRole()
        ));
    }


    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            Authentication auth,
            @Valid @RequestBody ChangePasswordRequest req
    ) {
        String username = auth.getName();
        userService.changeMyPassword(username, req.getCurrentPassword(), req.getNewPassword());
        return ResponseEntity.noContent().build();
    }

}