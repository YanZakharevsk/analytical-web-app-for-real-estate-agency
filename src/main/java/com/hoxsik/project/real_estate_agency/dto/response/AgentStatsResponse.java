package com.hoxsik.project.real_estate_agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AgentStatsResponse {
    private List<AgentPercentDto> offers;
    private List<AgentPercentDto> archived;
}
