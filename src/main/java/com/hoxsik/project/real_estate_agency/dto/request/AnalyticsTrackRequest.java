package com.hoxsik.project.real_estate_agency.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AnalyticsTrackRequest {

    @NotEmpty
    @Valid
    private List<AnalyticsTrackEventItem> events;
}
