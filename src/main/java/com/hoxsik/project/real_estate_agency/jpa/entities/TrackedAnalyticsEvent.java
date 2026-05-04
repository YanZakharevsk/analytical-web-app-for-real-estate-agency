package com.hoxsik.project.real_estate_agency.jpa.entities;

import com.hoxsik.project.real_estate_agency.jpa.entities.enums.AnalyticsEventCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tracked_analytics_event", schema = "real_estate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackedAnalyticsEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant serverTimestamp;

    @Column(nullable = false, length = 128)
    private String eventName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AnalyticsEventCategory category;

    private Instant clientTimestamp;

    @Column(length = 64)
    private String sessionId;

    private Long userId;

    @Column(length = 32)
    private String userRole;

    @Column(columnDefinition = "text", nullable = false)
    private String payloadJson;
}
