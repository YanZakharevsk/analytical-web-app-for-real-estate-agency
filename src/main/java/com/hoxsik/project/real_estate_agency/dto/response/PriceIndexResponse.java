package com.hoxsik.project.real_estate_agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PriceIndexResponse {
    private List<PriceIndexPointResponse> points;
    private PriceIndexSummaryResponse summary;
}

