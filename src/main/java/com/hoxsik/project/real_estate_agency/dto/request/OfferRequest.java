package com.hoxsik.project.real_estate_agency.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OfferRequest {
    @NotNull(message = "Цена не может быть null")
    @Positive(message = "Цена должна быть положительным числом")
    private Double price;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 20, message = "Описание должно содержать минимум 20 символов")
    private String description;
}
