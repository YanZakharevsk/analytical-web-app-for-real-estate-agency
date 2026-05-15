package com.hoxsik.project.real_estate_agency.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PriceDynamicsRequest {
    @NotBlank(message = "Сегмент обязателен")
    @Pattern(regexp = "ALL|ROOMS", flags = Pattern.Flag.CASE_INSENSITIVE, message = "segment должен быть ALL или ROOMS")
    private String segment;

    private Integer rooms;

    @NotBlank(message = "Индекс цены обязателен")
    @Pattern(regexp = "TOTAL|PER_M2", flags = Pattern.Flag.CASE_INSENSITIVE, message = "priceIndex должен быть TOTAL или PER_M2")
    private String priceIndex;
}
