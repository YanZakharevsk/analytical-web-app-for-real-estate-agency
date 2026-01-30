package com.hoxsik.courseproject.real_estate_agency.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull(message = "Рейтинг не может быть null")
    @Min(value = 1, message = "Рейтинг должен быть не менее 1")
    @Max(value = 10, message = "Рейтинг должен быть не более 10")
    private Integer rating;

    @NotNull(message = "Комментарий не может быть null")
    @NotBlank(message = "Комментарий не может быть пустым")
    private String comment;
}
