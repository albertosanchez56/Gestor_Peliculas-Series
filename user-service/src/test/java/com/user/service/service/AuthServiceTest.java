package com.user.service.service;

import com.user.service.dto.LoginRequestDTO;
import com.user.service.dto.RegisterRequestDTO;
import com.user.service.exception.ApiException;
import com.user.service.repositorio.UserRepository;
import com.user.service.servicio.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired AuthService authService;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void login_wrong_password_throws_401() {
        RegisterRequestDTO reg = new RegisterRequestDTO();
        reg.setEmail("a@mail.com");
        reg.setUsername("a");
        reg.setPassword("12345678");
        reg.setDisplayName("A");
        authService.register(reg);

        LoginRequestDTO login = new LoginRequestDTO();
        login.setLogin("a");
        login.setPassword("xxxxxxxx"); // incorrecta

        ApiException ex = assertThrows(ApiException.class, () -> authService.login(login));
        assertEquals(401, ex.getStatus().value());
        assertEquals("Invalid credentials", ex.getMessage());
    }
}