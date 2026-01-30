package com.hoxsik.courseproject.real_estate_agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentPriceSummaryResponse {
    private RentPriceSummaryItem oneRoom;
    private RentPriceSummaryItem twoRooms;
    private RentPriceSummaryItem threeRooms;
}
