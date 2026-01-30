package com.hoxsik.courseproject.real_estate_agency.dto.response;

import lombok.Data;

@Data
public class RentPricePointResponse {

    private String month;        // "2025-03"
    private Double oneRoom;
    private Double twoRooms;
    private Double threeRooms;

    public RentPricePointResponse(
            String month,
            Double oneRoom,
            Double twoRooms,
            Double threeRooms
    ) {
        this.month = month;
        this.oneRoom = oneRoom;
        this.twoRooms = twoRooms;
        this.threeRooms = threeRooms;
    }
}
