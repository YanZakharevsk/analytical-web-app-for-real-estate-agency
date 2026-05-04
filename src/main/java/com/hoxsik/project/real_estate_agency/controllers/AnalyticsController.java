package com.hoxsik.project.real_estate_agency.controllers;

import com.hoxsik.project.real_estate_agency.dto.request.PriceDynamicsRequest;
import com.hoxsik.project.real_estate_agency.dto.response.AgentStatsResponse;
import com.hoxsik.project.real_estate_agency.dto.response.PriceDynamicsResponse;
import com.hoxsik.project.real_estate_agency.dto.response.PriceIndexResponse;
import com.hoxsik.project.real_estate_agency.dto.response.RentPriceResponse;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.project.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.project.real_estate_agency.services.AnalyticsService;
import com.hoxsik.project.real_estate_agency.services.AnalyticsTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AnalyticsTrackingService analyticsTrackingService;

    @RequiredPrivilege(Privilege.CHECK_ANALYTICS)
    @PostMapping("/price-dynamics")
    public List<PriceDynamicsResponse> getPriceDynamics(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PriceDynamicsRequest request
    ) {
        List<PriceDynamicsResponse> result = analyticsService.getPriceDynamics(request);
        Map<String, Object> props = new HashMap<>();
        props.put("segment_type", request.getSegment());
        props.put("index_type", request.getPriceIndex());
        props.put("records_count", result.size());
        analyticsTrackingService.recordSystemEvent("analytics_data_loaded", props, userDetails);
        return result;
    }

    @RequiredPrivilege(Privilege.CHECK_ANALYTICS)
    @GetMapping("/apartment-price-index")
    public PriceIndexResponse getApartmentPriceIndex(@AuthenticationPrincipal UserDetails userDetails) {
        PriceIndexResponse response = analyticsService.getApartmentPriceIndex();
        Map<String, Object> props = new HashMap<>();
        props.put("segment_type", "APARTMENT_INDEX");
        props.put("index_type", "APARTMENT");
        props.put("records_count", response.getPoints() != null ? response.getPoints().size() : 0);
        analyticsTrackingService.recordSystemEvent("analytics_data_loaded", props, userDetails);
        return response;
    }

    @RequiredPrivilege(Privilege.CHECK_ANALYTICS)
    @GetMapping("/rent-price-index")
    public RentPriceResponse getRentPrices(@AuthenticationPrincipal UserDetails userDetails) {
        RentPriceResponse response = analyticsService.getRentPriceIndex();
        Map<String, Object> props = new HashMap<>();
        props.put("segment_type", "RENT");
        props.put("index_type", "RENT");
        props.put("records_count", response.getPoints() != null ? response.getPoints().size() : 0);
        analyticsTrackingService.recordSystemEvent("analytics_data_loaded", props, userDetails);
        return response;
    }

    @RequiredPrivilege(Privilege.CHECK_ANALYTICS)
    @GetMapping("/agents")
    public AgentStatsResponse getAgentStats(@AuthenticationPrincipal UserDetails userDetails) {
        AgentStatsResponse response = analyticsService.getAgentStats();
        int count = 0;
        if (response.getOffers() != null) {
            count += response.getOffers().size();
        }
        if (response.getArchived() != null) {
            count += response.getArchived().size();
        }
        Map<String, Object> props = new HashMap<>();
        props.put("segment_type", "AGENTS");
        props.put("index_type", "OFFER_SHARE");
        props.put("records_count", count);
        analyticsTrackingService.recordSystemEvent("analytics_data_loaded", props, userDetails);
        return response;
    }
}

