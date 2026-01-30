package com.hoxsik.courseproject.real_estate_agency.dto.request;

import lombok.Data;

@Data
public class PriceDynamicsRequest {
    private String segment;     // ALL | ROOMS
    private Integer rooms;      // nullable
    private String priceIndex;  // TOTAL | PER_M2
}
