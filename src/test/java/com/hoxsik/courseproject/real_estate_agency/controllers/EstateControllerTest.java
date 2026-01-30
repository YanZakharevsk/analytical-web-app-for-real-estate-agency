package com.hoxsik.courseproject.real_estate_agency.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoxsik.courseproject.real_estate_agency.dto.request.EstateRequest;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.*;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.Role;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.*;
import com.hoxsik.courseproject.real_estate_agency.jpa.repositories.*;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EstateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EstateRepository estateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private AgentRepository agentRepository;

    private User ownerUser;
    private Agent testAgent;

    @BeforeEach
    void setUp() {
        // Создание владельца
        ownerUser = new User();
        ownerUser.setUsername("estate_owner");
        ownerUser.setPassword("password");
        ownerUser.setRole(Role.OWNER);
        userRepository.save(ownerUser);

        Owner owner = new Owner();
        owner.setUser(ownerUser);
        ownerRepository.save(owner);

        // Создание агента
        User agentUser = new User();
        agentUser.setUsername("estate_agent");
        agentUser.setPassword("password");
        agentUser.setRole(Role.AGENT);
        userRepository.save(agentUser);

        testAgent = new Agent();
        testAgent.setUser(agentUser);
        agentRepository.save(testAgent);
    }

    // TC-3: Добавление объекта недвижимости
    @Test
    @WithMockUser(username = "estate_owner")
    void testReportEstate_Success() throws Exception {
        // Arrange
        EstateRequest request = new EstateRequest();
        request.setType(EstateType.APARTMENT);
        request.setBathrooms(1);
        request.setRooms(2);
        request.setGarage(false);
        request.setStorey(3);
        request.setLocation("Минск, пр. Независимости 1");
        request.setBalcony(true);
        request.setDescription("Светлая квартира с ремонтом");
        request.setAvailability(Availability.FOR_SALE);
        request.setSize(55.5);
        request.setCondition(Condition.NORMAL_USE_SIGNS);
        request.setOfferedPrice(85000.0);
        request.setAgent(testAgent.getId());

        // Act & Assert
        mockMvc.perform(post("/api/owner/report-offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Проверка в базе данных
        Optional <List<Estate>> savedEstate = estateRepository.findByOwnerUsername("estate_owner");
        assertTrue(savedEstate.isPresent());
        assertEquals("Минск, пр. Независимости 1", savedEstate.get());
    }
}