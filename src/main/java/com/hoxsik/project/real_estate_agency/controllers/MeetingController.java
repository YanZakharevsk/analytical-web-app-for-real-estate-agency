package com.hoxsik.project.real_estate_agency.controllers;

import com.hoxsik.project.real_estate_agency.dto.Mapper;
import com.hoxsik.project.real_estate_agency.dto.response.MeetingResponse;
import com.hoxsik.project.real_estate_agency.dto.response.Response;
import com.hoxsik.project.real_estate_agency.jpa.entities.Meeting;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Role;
import com.hoxsik.project.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.project.real_estate_agency.services.MeetingService;
import lombok.NonNull;
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
public class MeetingController {
    private final MeetingService meetingService;

    @RequiredPrivilege(Privilege.SCHEDULE_MEETING)
    @PostMapping("/schedule-meeting")
    public ResponseEntity<Response> scheduleMeeting(@AuthenticationPrincipal UserDetails userDetails, @NonNull @RequestParam("id") Long id) {
        Response response = meetingService.scheduleMeeting(userDetails.getUsername(), id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @RequiredPrivilege(Privilege.CHECK_MEETINGS)
    @GetMapping("/scheduled-meetings")
    public ResponseEntity<List<MeetingResponse>> checkScheduledMeetings(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<List<Meeting>> optionalMeetings = meetingService.getMeetings(userDetails.getUsername());

        return optionalMeetings.
                map(meetings -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(meetings.stream().map(Mapper.INSTANCE::convertMeetings).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @RequiredPrivilege(Privilege.CHECK_MEETINGS)
    @GetMapping("/agent/scheduled-meetings")
    public ResponseEntity<List<MeetingResponse>> checkAgentScheduledMeetings(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<List<Meeting>> optionalMeetings = meetingService.getAgentScheduledMeetings(userDetails.getUsername());

        return optionalMeetings.
                map(meetings -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(meetings.stream().map(Mapper.INSTANCE::convertMeetings).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @RequiredPrivilege(Privilege.CHECK_MEETINGS)
    @GetMapping("/agent/scheduled-meetings/{role}")
    public ResponseEntity<List<MeetingResponse>> checkAgentScheduledMeetingsByRole(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("role") String role) {
        Optional<List<Meeting>> optionalMeetings = meetingService.getAgentScheduledMeetingByRole(userDetails.getUsername(), Role.valueOf(role.toUpperCase()));

        return optionalMeetings.
                map(meetings -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(meetings.stream().map(Mapper.INSTANCE::convertMeetings).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }
}
