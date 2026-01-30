package com.hoxsik.courseproject.real_estate_agency.controllers;

import com.hoxsik.courseproject.real_estate_agency.dto.Mapper;
import com.hoxsik.courseproject.real_estate_agency.dto.request.CalendarRequest;
import com.hoxsik.courseproject.real_estate_agency.dto.response.CalendarResponse;
import com.hoxsik.courseproject.real_estate_agency.dto.response.Response;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Calendar;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.courseproject.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.courseproject.real_estate_agency.services.CalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    @RequiredPrivilege(Privilege.ADD_MEETINGS)
    @PostMapping("/agent/add-meeting-slots")
    public ResponseEntity<Response> addMeetingSlots(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CalendarRequest calendarRequest) {
        Response response = calendarService.addAvailableMeetings(userDetails.getUsername(), calendarRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/auth/calendar")
    public ResponseEntity<List<CalendarResponse>> checkAgentCalendar(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("id") Long id) {
        Optional<List<Calendar>> optionalCalendars = calendarService.checkAgentCalendar(id);

        return optionalCalendars
                .map(calendars -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(calendars.stream().map(Mapper.INSTANCE::convertCalendar).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }
}
