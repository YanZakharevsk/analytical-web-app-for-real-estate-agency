package com.hoxsik.courseproject.real_estate_agency.services;

import com.hoxsik.courseproject.real_estate_agency.dto.request.CalendarRequest;
import com.hoxsik.courseproject.real_estate_agency.dto.response.Response;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Agent;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Calendar;
import com.hoxsik.courseproject.real_estate_agency.jpa.repositories.CalendarRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final AgentService agentService;

    @Transactional
    public Response addAvailableMeetings(String username, CalendarRequest calendarRequest){
        Optional<Agent> optionalAgent = agentService.getByUsername(username);

        if (optionalAgent.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Агент с указанным именем не найден");

        Agent agent = optionalAgent.get();

        calendarRequest.getSlots().forEach(slot ->{
            Calendar calendar = new Calendar();
            calendar.setAgent(agent);
            calendar.setDate(slot);
            calendarRepository.save(calendar);
        });

        return new Response(true, HttpStatus.CREATED, "Успешное добавление доступных слотов");
    }

    /**
     * Retrieves the agent's calendar
     * @param id ID of the agent to have the calendar checked
     * @return List of available slots if present
     */
    public Optional<List<Calendar>> checkAgentCalendar(Long id) {
        Optional<Agent> agent = agentService.getByID(id);

        if (agent.isEmpty())
            return Optional.empty();

        return calendarRepository.findByAgent(agent.get());
    }

    public Optional<Calendar> getByID(Long id) {
        return calendarRepository.findById(id);
    }

    public void deleteCalendar(Calendar calendar) {
        calendarRepository.delete(calendar);
    }
}
