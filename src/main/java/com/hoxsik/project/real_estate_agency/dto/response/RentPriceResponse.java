package com.hoxsik.project.real_estate_agency.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class RentPriceResponse {
    private List<RentPricePointResponse> points;
    private RentPriceSummaryResponse summary;
}


