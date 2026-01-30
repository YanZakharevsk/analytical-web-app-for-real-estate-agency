package com.hoxsik.courseproject.real_estate_agency.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoxsik.courseproject.real_estate_agency.dto.request.PriceDynamicsRequest;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Agent;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.User;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.Role;
import com.hoxsik.courseproject.real_estate_agency.jpa.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    // TC-9: Получение аналитики цен
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetPriceDynamics_Success() throws Exception {
        // Arrange
        PriceDynamicsRequest request = new PriceDynamicsRequest();
        request.setSegment("ALL");
        request.setPriceIndex("TOTAL");

        // Act & Assert
        mockMvc.perform(post("/api/analytics/price-dynamics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAgentStats_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/analytics/agents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offers").isArray())
                .andExpect(jsonPath("$.archived").isArray());
    }
}