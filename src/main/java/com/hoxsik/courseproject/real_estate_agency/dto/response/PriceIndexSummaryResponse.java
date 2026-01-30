package com.hoxsik.courseproject.real_estate_agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceIndexSummaryResponse {
    private double lastIndex;          // USD/м²
    private double monthChangePercent; // %
    private double totalChangePercent; // %
}

