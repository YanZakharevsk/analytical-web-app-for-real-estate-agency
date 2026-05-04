package com.hoxsik.project.real_estate_agency.dto.response;

import com.hoxsik.project.real_estate_agency.jpa.entities.enums.AnalyticsEventCategory;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class TrackedAnalyticsEventResponse {
    Long id;
    Instant serverTimestamp;
    String eventName;
    AnalyticsEventCategory category;
    Instant clientTimestamp;
    String sessionId;
    Long userId;
    String userRole;
    String payloadJson;
}
