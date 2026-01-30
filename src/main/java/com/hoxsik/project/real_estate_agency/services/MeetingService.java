package com.hoxsik.project.real_estate_agency.services;

import com.hoxsik.project.real_estate_agency.dto.response.Response;
import com.hoxsik.project.real_estate_agency.jpa.entities.Calendar;
import com.hoxsik.project.real_estate_agency.jpa.entities.Meeting;
import com.hoxsik.project.real_estate_agency.jpa.entities.User;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Role;
import com.hoxsik.project.real_estate_agency.jpa.repositories.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final CalendarService calendarService;
    private final MeetingRepository meetingRepository;
    private final UserService userService;

    @Transactional
    public Response scheduleMeeting(String username, Long id) {
        Optional<User> user = userService.getByUsername(username);

        if (user.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Учетная запись с указанным именем пользователя не найдена");

        Optional<Calendar> calendar = calendarService.getByID(id);

        if (calendar.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Доступная встреча не найдена");

        Meeting meeting = new Meeting();
        meeting.setUser(user.get());
        meeting.setAgent(calendar.get().getAgent());
        meeting.setDate(calendar.get().getDate());
        meeting.setRole(user.get().getRole());

        meetingRepository.save(meeting);

        return new Response(true, HttpStatus.CREATED, "Успешно запланировали встречу");
    }

    public Optional<List<Meeting>> getAgentScheduledMeetings(String username) {
        return meetingRepository.findByAgentUsername(username);
    }

    public Optional<List<Meeting>> getAgentScheduledMeetingByRole(String username, Role role) {
        return meetingRepository.findByAgentUsernameAndRole(username, role);
    }

    public Optional<List<Meeting>> getMeetings(String username) {
        return meetingRepository.findByUsername(username);
    }

}
