package com.hoxsik.courseproject.real_estate_agency.dto.request;

import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.Condition;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.EstateType;
import lombok.Data;

@Data
public class EstimateRequestDto {
    private EstateType estateType;
    private Availability availability;
    private Condition condition;

    private Integer rooms;
    private Double area;

    private Integer floor;
    private Integer totalFloors;
}
