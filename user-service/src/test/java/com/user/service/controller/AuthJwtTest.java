package com.user.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.service.dto.LoginRequestDTO;
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


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthJwtTest {

	@Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void me_sin_token_devuelve_401() throws Exception {
        mvc.perform(get("/usuario/auth/me"))
           .andExpect(status().isUnauthorized());
    }

    @Test
    void me_con_token_devuelve_200_y_datos_usuario() throws Exception {
        // 1) REGISTER
        RegisterRequestDTO reg = new RegisterRequestDTO();
        reg.setEmail("alberto@mail.com");
        reg.setUsername("alberto56");
        reg.setPassword("12345678");
        reg.setDisplayName("Alberto");

        mvc.perform(post("/usuario/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
           .andExpect(status().isOk());

        // 2) LOGIN -> obtener token
        LoginRequestDTO login = new LoginRequestDTO();
        login.setLogin("alberto56");
        login.setPassword("12345678");

        String loginJson = mvc.perform(post("/usuario/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.accessToken").exists())
           .andReturn()
           .getResponse()
           .getContentAsString();

        JsonNode node = objectMapper.readTree(loginJson);
        String token = node.get("accessToken").asText();

        // 3) /me con Bearer token
        mvc.perform(get("/usuario/auth/me")
                .header("Authorization", "Bearer " + token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username").value("alberto56"))
           .andExpect(jsonPath("$.role").value("USER"));
    }
}