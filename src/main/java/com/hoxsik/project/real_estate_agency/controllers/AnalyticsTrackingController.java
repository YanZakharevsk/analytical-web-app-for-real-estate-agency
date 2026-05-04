package com.hoxsik.project.real_estate_agency.controllers;

import com.hoxsik.project.real_estate_agency.dto.request.AnalyticsTrackRequest;
import com.hoxsik.project.real_estate_agency.dto.response.TrackedAnalyticsEventResponse;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.project.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.project.real_estate_agency.services.AnalyticsTrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsTrackingController {

    private final AnalyticsTrackingService analyticsTrackingService;

    @PostMapping("/track")
    public ResponseEntity<Void> track(
            @Valid @RequestBody AnalyticsTrackRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        analyticsTrackingService.ingest(request, userDetails);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @RequiredPrivilege(Privilege.CHECK_ANALYTICS)
    @GetMapping("/track/recent")
    public List<TrackedAnalyticsEventResponse> recent() {
        return analyticsTrackingService.recent();
    }
}
