package com.hoxsik.project.real_estate_agency.dto.request;

import com.hoxsik.project.real_estate_agency.jpa.entities.enums.AnalyticsEventCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AnalyticsTrackEventItem {

    @NotBlank
    private String eventName;

    @NotNull
    private AnalyticsEventCategory category;

    private Instant clientTimestamp;

    private String sessionId;

    private Map<String, Object> properties = new LinkedHashMap<>();
}
