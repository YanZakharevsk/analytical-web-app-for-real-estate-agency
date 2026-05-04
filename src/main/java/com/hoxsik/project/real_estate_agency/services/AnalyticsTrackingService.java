package com.hoxsik.project.real_estate_agency.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoxsik.project.real_estate_agency.dto.request.AnalyticsTrackEventItem;
import com.hoxsik.project.real_estate_agency.dto.request.AnalyticsTrackRequest;
import com.hoxsik.project.real_estate_agency.dto.response.TrackedAnalyticsEventResponse;
import com.hoxsik.project.real_estate_agency.jpa.entities.TrackedAnalyticsEvent;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.AnalyticsEventCategory;
import com.hoxsik.project.real_estate_agency.jpa.repositories.TrackedAnalyticsEventRepository;
import com.hoxsik.project.real_estate_agency.security.DatabaseUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsTrackingService {

    private final TrackedAnalyticsEventRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void recordSystemEvent(String eventName, Map<String, Object> properties, UserDetails principal) {
        AnalyticsTrackEventItem item = new AnalyticsTrackEventItem();
        item.setEventName(eventName);
        item.setCategory(AnalyticsEventCategory.SYSTEM);
        item.setClientTimestamp(Instant.now());
        item.setProperties(properties != null ? new LinkedHashMap<>(properties) : new LinkedHashMap<>());
        AnalyticsTrackRequest request = new AnalyticsTrackRequest();
        request.setEvents(List.of(item));
        ingest(request, principal);
    }

    @Transactional
    public void ingest(AnalyticsTrackRequest request, UserDetails principal) {
        Long userId = null;
        String userRole = null;
        if (principal instanceof DatabaseUserDetails details) {
            userId = details.getUser().getId();
            userRole = details.getUser().getRole().name();
        }

        Instant serverNow = Instant.now();
        List<TrackedAnalyticsEvent> batch = new ArrayList<>();
        for (AnalyticsTrackEventItem item : request.getEvents()) {
            Map<String, Object> merged = new LinkedHashMap<>(sanitize(item.getProperties()));
            if (userId != null) {
                merged.putIfAbsent("user_id", userId);
            }
            if (userRole != null) {
                merged.putIfAbsent("user_role", userRole);
            }
            merged.put("timestamp", item.getClientTimestamp() != null ? item.getClientTimestamp().toString() : serverNow.toString());

            String json;
            try {
                json = objectMapper.writeValueAsString(merged);
            } catch (JsonProcessingException e) {
                json = "{\"error\":\"payload_serialization_failed\"}";
            }

            batch.add(TrackedAnalyticsEvent.builder()
                    .serverTimestamp(serverNow)
                    .eventName(item.getEventName())
                    .category(item.getCategory())
                    .clientTimestamp(item.getClientTimestamp())
                    .sessionId(truncate(item.getSessionId(), 64))
                    .userId(userId)
                    .userRole(truncate(userRole, 32))
                    .payloadJson(json)
                    .build());
        }
        repository.saveAll(batch);
    }

    @Transactional(readOnly = true)
    public List<TrackedAnalyticsEventResponse> recent() {
        return repository.findTop200ByOrderByServerTimestampDesc().stream()
                .map(e -> TrackedAnalyticsEventResponse.builder()
                        .id(e.getId())
                        .serverTimestamp(e.getServerTimestamp())
                        .eventName(e.getEventName())
                        .category(e.getCategory())
                        .clientTimestamp(e.getClientTimestamp())
                        .sessionId(e.getSessionId())
                        .userId(e.getUserId())
                        .userRole(e.getUserRole())
                        .payloadJson(e.getPayloadJson())
                        .build())
                .collect(Collectors.toList());
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> sanitize(Map<String, Object> raw) {
        if (raw == null || raw.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : raw.entrySet()) {
            String key = e.getKey();
            if (key == null) {
                continue;
            }
            if (isSensitiveKey(key)) {
                out.put(key, "[redacted]");
                continue;
            }
            Object value = e.getValue();
            if (value instanceof Map<?, ?> nested) {
                out.put(key, sanitize((Map<String, Object>) nested));
            } else {
                out.put(key, value);
            }
        }
        return out;
    }

    private static boolean isSensitiveKey(String key) {
        String lower = key.toLowerCase(Locale.ROOT);
        return lower.contains("password") || lower.contains("secret") || lower.contains("token");
    }
}
