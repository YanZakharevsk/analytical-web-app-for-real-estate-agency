package com.hoxsik.project.real_estate_agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentPriceSummaryItem {
    private double monthChange;
    private double totalChange;
}
