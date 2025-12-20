package com.user.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.service.dto.RegisterRequestDTO;
import com.user.service.repositorio.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void register_ok_returns_200_and_user_data() throws Exception {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail("alberto@mail.com");
        req.setUsername("alberto56");
        req.setPassword("12345678");
        req.setDisplayName("Alberto");

        mvc.perform(post("/usuario/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.username").value("alberto56"))
            .andExpect(jsonPath("$.displayName").value("Alberto"))
            .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void register_duplicate_email_returns_409() throws Exception {
        // 1º registro
        RegisterRequestDTO req1 = new RegisterRequestDTO();
        req1.setEmail("alberto@mail.com");
        req1.setUsername("alberto56");
        req1.setPassword("12345678");
        req1.setDisplayName("Alberto");

        mvc.perform(post("/usuario/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
            .andExpect(status().isOk());

        // 2º registro con mismo email
        RegisterRequestDTO req2 = new RegisterRequestDTO();
        req2.setEmail("alberto@mail.com");
        req2.setUsername("otroNick");
        req2.setPassword("12345678");
        req2.setDisplayName("Otro");

        mvc.perform(post("/usuario/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Email already in use"));
    }
}
