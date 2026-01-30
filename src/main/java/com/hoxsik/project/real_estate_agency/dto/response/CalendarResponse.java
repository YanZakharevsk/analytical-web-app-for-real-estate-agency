package com.hoxsik.project.real_estate_agency.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalendarResponse {
    private Long id;
    private Long agentID;
    private LocalDateTime date;
}
