package com.hoxsik.courseproject.real_estate_agency.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoxsik.courseproject.real_estate_agency.dto.request.UserRequest;
import com.hoxsik.courseproject.real_estate_agency.dto.response.Response;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.User;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.Role;
import com.hoxsik.courseproject.real_estate_agency.jpa.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    // TC-1: Регистрация владельца недвижимости
    @Test
    void testOwnerRegistration_Success() throws Exception {
        // Arrange
        UserRequest request = new UserRequest();
        request.setFirstName("Иван");
        request.setLastName("Иванов");
        request.setUsername("ivanov_test");
        request.setPassword("Password123");
        request.setEmail("ivanov_test@example.com");
        request.setPhoneNumber("+375291111111");

        // Act & Assert
        mockMvc.perform(post("/api/auth/owner/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Успешное создание пользовательского аккаунта"));

        // Проверка в базе данных
        Optional<User> savedUser = userRepository.findByUsername("ivanov_test");
        assertTrue(savedUser.isPresent());
        assertEquals(Role.OWNER, savedUser.get().getRole());
        assertEquals("Иван", savedUser.get().getFirstName());
    }

    @Test
    void testOwnerRegistration_DuplicateUsername() throws Exception {
        // Arrange - создаем пользователя с таким же username
        User existingUser = new User();
        existingUser.setUsername("existing_user");
        existingUser.setPassword(passwordEncoder.encode("password"));
        existingUser.setRole(Role.CUSTOMER);
        userRepository.save(existingUser);

        UserRequest request = new UserRequest();
        request.setFirstName("Петр");
        request.setLastName("Петров");
        request.setUsername("existing_user"); // Дубликат username
        request.setPassword("Password123");
        request.setEmail("petrov@example.com");
        request.setPhoneNumber("+375292222222");

        // Act & Assert
        mockMvc.perform(post("/api/auth/owner/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}