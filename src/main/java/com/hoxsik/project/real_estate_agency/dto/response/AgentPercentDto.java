package com.hoxsik.project.real_estate_agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentPercentDto {
    private String agentName;
    private double percent;
}
