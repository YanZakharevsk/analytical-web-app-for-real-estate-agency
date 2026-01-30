package com.hoxsik.project.real_estate_agency.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoxsik.project.real_estate_agency.dto.request.OfferRequest;
import com.hoxsik.project.real_estate_agency.jpa.entities.*;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Role;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Condition;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.EstateType;
import com.hoxsik.project.real_estate_agency.jpa.repositories.*;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private EstateRepository estateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private AgentRepository agentRepository;

    private Estate testEstate;
    private User agentUser;
    private User customerUser;

    @BeforeEach
    void setUp() {
        // Создание тестовых данных
        User ownerUser = new User();
        ownerUser.setUsername("owner_test");
        ownerUser.setPassword("password");
        ownerUser.setRole(Role.OWNER);
        userRepository.save(ownerUser);

        Owner owner = new Owner();
        owner.setUser(ownerUser);
        ownerRepository.save(owner);

        agentUser = new User();
        agentUser.setUsername("agent_test");
        agentUser.setPassword("password");
        agentUser.setRole(Role.AGENT);
        userRepository.save(agentUser);

        Agent agent = new Agent();
        agent.setUser(agentUser);
        agentRepository.save(agent);

        customerUser = new User();
        customerUser.setUsername("customer_test");
        customerUser.setPassword("password");
        customerUser.setRole(Role.CUSTOMER);
        userRepository.save(customerUser);

        // Создание объекта недвижимости
        testEstate = new Estate();
        testEstate.setOwner(owner);
        testEstate.setAgent(agent);
        testEstate.setType(EstateType.APARTMENT);
        testEstate.setRooms(2);
        testEstate.setBathrooms(1);
        testEstate.setSize(50.0);
        testEstate.setLocation("Минск");
        testEstate.setOfferedPrice(100000.0);
        testEstate.setAvailability(Availability.FOR_SALE);
        testEstate.setCondition(Condition.NEEDS_RENOVATION);
        testEstate.setPostDate(LocalDateTime.now());
        testEstate.setIsSubmitted(false);
        estateRepository.save(testEstate);
    }

    // TC-4: Публикация предложения агентом
    @Test
    @WithMockUser(username = "agent_test", roles = {"AGENT"})
    void testPostOffer_Success() throws Exception {
        // Arrange
        OfferRequest request = new OfferRequest();
        request.setPrice(120000.0);
        request.setDescription("Отличная квартира в центре");

        // Act & Assert
        mockMvc.perform(post("/api/agent/post-offer")
                        .param("id", testEstate.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        // Проверка, что предложение создано
        assertTrue(offerRepository.findByEstate(testEstate).getPrice() == 120000.0);
    }

    // TC-6: Бронирование предложения клиентом
    @Test
    @WithMockUser(username = "customer_test", roles = {"CUSTOMER"})
    void testBlockOffer_Success() throws Exception {
        // Arrange - сначала создаем предложение
        Offer offer = new Offer();
        offer.setEstate(testEstate);
        offer.setPrice(120000.0);
        offer.setDescription("Тестовое предложение");
        offer.setPostDate(LocalDateTime.now());
        offerRepository.save(offer);

        // Act & Assert
        mockMvc.perform(patch("/api/customer/block-offer")
                        .param("id", offer.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Проверка, что предложение заблокировано
        Offer blockedOffer = offerRepository.findById(offer.getId()).get();
        assertTrue(blockedOffer.isBlocked());
    }

    // TC-8: Добавление в избранное
    @Test
    @WithMockUser(username = "customer_test", roles = {"CUSTOMER"})
    void testAddToFavorites_Success() throws Exception {
        // Arrange
        Offer offer = new Offer();
        offer.setEstate(testEstate);
        offer.setPrice(120000.0);
        offerRepository.save(offer);

        // Act & Assert
        mockMvc.perform(post("/api/customer/add-to-favorites")
                        .param("id", offer.getId().toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }
}