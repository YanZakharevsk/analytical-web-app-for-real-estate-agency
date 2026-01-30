package com.hoxsik.courseproject.real_estate_agency.controllers;

import com.hoxsik.courseproject.real_estate_agency.jpa.entities.*;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.Role;
import com.hoxsik.courseproject.real_estate_agency.jpa.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    private Calendar testCalendar;

    @BeforeEach
    void setUp() {
        // Создание агента
        User agentUser = new User();
        agentUser.setUsername("meeting_agent");
        agentUser.setPassword("password");
        agentUser.setRole(Role.AGENT);
        userRepository.save(agentUser);

        Agent agent = new Agent();
        agent.setUser(agentUser);
        agentRepository.save(agent);

        // Создание клиента
        User customerUser = new User();
        customerUser.setUsername("meeting_customer");
        customerUser.setPassword("password");
        customerUser.setRole(Role.CUSTOMER);
        userRepository.save(customerUser);

        // Создание календарного слота
        testCalendar = new Calendar();
        testCalendar.setAgent(agent);
        testCalendar.setDate(LocalDateTime.now().plusDays(1));
        calendarRepository.save(testCalendar);
    }

    // TC-10: Запись на встречу
    @Test
    @WithMockUser(username = "meeting_customer", roles = {"CUSTOMER"})
    void testScheduleMeeting_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/schedule-meeting")
                        .param("id", testCalendar.getId().toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        // Проверка, что встреча создана
        assertFalse(meetingRepository.findByUsername("meeting_customer").get().isEmpty());
    }

    @Test
    @WithMockUser(username = "meeting_customer", roles = {"CUSTOMER"})
    void testCheckScheduledMeetings_Success() throws Exception {
        // Arrange - сначала записываемся на встречу
        mockMvc.perform(post("/api/schedule-meeting")
                .param("id", testCalendar.getId().toString()));

        // Act & Assert - проверяем запланированные встречи
        mockMvc.perform(get("/api/scheduled-meetings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].date").exists());
    }

    @Test
    @WithMockUser(username = "meeting_agent", roles = {"AGENT"})
    void testCheckAgentScheduledMeetings_Success() throws Exception {
        // Arrange - сначала клиент записывается на встречу
        User customerUser = userRepository.findByUsername("meeting_customer").get();
        Meeting meeting = new Meeting();
        meeting.setUser(customerUser);
        meeting.setAgent(testCalendar.getAgent());
        meeting.setDate(testCalendar.getDate());
        meeting.setRole(Role.CUSTOMER);
        meetingRepository.save(meeting);

        // Act & Assert - агент проверяет свои встречи
        mockMvc.perform(get("/api/agent/scheduled-meetings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}