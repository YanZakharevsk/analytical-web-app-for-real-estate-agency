package com.hoxsik.project.real_estate_agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceDynamicsResponse {
    private String date;        // yyyy-MM
    private double price;       // средняя цена
    private Long count;         // количество аналогичных предложений
}
