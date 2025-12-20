package com.user.service.servicio;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.service.Entidades.Status;
import com.user.service.Entidades.User;
import com.user.service.dto.AuthResponseDTO;
import com.user.service.dto.LoginRequestDTO;
import com.user.service.dto.RegisterRequestDTO;
import com.user.service.dto.RegisterResponseDTO;
import com.user.service.dto.UserInfoDTO;
import com.user.service.exception.ApiException;
import com.user.service.repositorio.UserRepository;
import com.user.service.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
    }

    public RegisterResponseDTO register(RegisterRequestDTO req) {
        String email = req.getEmail().trim();
        String username = req.getUsername().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already in use");
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already in use");
        }

        User u = new User();
        u.setEmail(email);
        u.setUsername(username);
        u.setDisplayName(req.getDisplayName().trim());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        User saved = userRepository.save(u);

        return new RegisterResponseDTO(saved.getId(), saved.getUsername(), saved.getDisplayName(), saved.getRole());
    }

    public AuthResponseDTO login(LoginRequestDTO req) {
        String login = req.getLogin().trim();

        User user = userRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(login, login)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (user.getStatus() == Status.BANNED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User is banned");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());

        UserInfoDTO info = new UserInfoDTO(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
        return new AuthResponseDTO(token, jwtService.getExpirationSeconds(), info);
    }

}