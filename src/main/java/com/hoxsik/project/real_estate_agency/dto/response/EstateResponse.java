package com.hoxsik.project.real_estate_agency.dto.response;

import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Condition;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.EstateType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EstateResponse {
    private Long id;
    private Long agentID;
    private String agent;
    private Long ownerID;
    private String owner;
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
    private Double offeredPrice;
    private LocalDateTime postDate;
    private Boolean isSubmitted;
}
