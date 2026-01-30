package com.hoxsik.project.real_estate_agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstimateResponseDto {

    private boolean found;

    private Double estimatedPrice;
    private Double percent;

    private String message;
}

