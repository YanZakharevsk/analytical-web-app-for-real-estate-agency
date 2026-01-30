package com.hoxsik.courseproject.real_estate_agency.controllers;

import com.hoxsik.courseproject.real_estate_agency.dto.request.PriceDynamicsRequest;
import com.hoxsik.courseproject.real_estate_agency.dto.response.AgentStatsResponse;
import com.hoxsik.courseproject.real_estate_agency.dto.response.PriceDynamicsResponse;
import com.hoxsik.courseproject.real_estate_agency.dto.response.PriceIndexResponse;
import com.hoxsik.courseproject.real_estate_agency.dto.response.RentPriceResponse;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.courseproject.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.courseproject.real_estate_agency.services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @RequiredPrivilege(Privilege.CHECK_ANALYTICS)
    @PostMapping("/price-dynamics")
    public List<PriceDynamicsResponse> getPriceDynamics(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PriceDynamicsRequest request
    ) {
        return analyticsService.getPriceDynamics(request);
    }

    @RequiredPrivilege(Privilege.CHECK_ANALYTICS)
    @GetMapping("/apartment-price-index")
    public PriceIndexResponse getApartmentPriceIndex() {
        return analyticsService.getApartmentPriceIndex();
    }

    @RequiredPrivilege(Privilege.CHECK_ANALYTICS)
    @GetMapping("/rent-price-index")
    public RentPriceResponse getRentPrices() {
        return analyticsService.getRentPriceIndex();
    }

    @RequiredPrivilege(Privilege.CHECK_ANALYTICS)
    @GetMapping("/agents")
    public AgentStatsResponse getAgentStats() {
        return analyticsService.getAgentStats();
    }
}

