package com.hoxsik.project.real_estate_agency.jpa.repositories;

import com.hoxsik.project.real_estate_agency.jpa.entities.TrackedAnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackedAnalyticsEventRepository extends JpaRepository<TrackedAnalyticsEvent, Long> {

    List<TrackedAnalyticsEvent> findTop200ByOrderByServerTimestampDesc();
}
