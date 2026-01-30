package com.hoxsik.project.real_estate_agency.dto.response;

import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Availability;
import lombok.Data;

@Data
public class OfferPreviewResponse {
    private Long id;
    private Long estateID;
    private String location;
    private Double price;
    private String description;
    private Availability availability;
}
