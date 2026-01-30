package com.hoxsik.project.real_estate_agency.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DocumentRequest {
    @NotNull(message = "ID имущества не может быть нулевым")
    @Positive(message = "ID имущества должно быть положительным")
    private Long estateID;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
