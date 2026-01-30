package com.hoxsik.courseproject.real_estate_agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.YearMonth;

@Data
@AllArgsConstructor
public class PriceIndexPointResponse {
    private String month;
    private Double secondaryIndex; // вторичка
    private Double newbuildIndex;  // новостройки
}

