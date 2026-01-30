package com.hoxsik.project.real_estate_agency.dto.response;

import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Condition;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.EstateType;
import lombok.Data;

@Data
public class OfferResponse {
    private Long id;
    private Long estateID;
    private Long agentID;
    private String agent;
    private EstateType type;
    private Integer bathrooms;
    private Integer rooms;
    private Boolean garage;
    private Integer storey;
    private String location;
    private Boolean balcony;
    private String description;
    private Availability availability;
    private Double size;
    private Condition condition;
    private Double price;
    private Boolean blocked;
    private String blockedBy;
}
