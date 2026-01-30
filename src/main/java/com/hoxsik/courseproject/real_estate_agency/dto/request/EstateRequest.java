package com.hoxsik.courseproject.real_estate_agency.dto.request;

import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.Condition;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.EstateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EstateRequest {
    @NotNull(message = "ID агента не может быть null")
    @Positive(message = "ID агента должен быть положительным числом")
    private Long agent;

    @NotNull(message = "Тип недвижимости не может быть null")
    private EstateType type;

    @NotNull(message = "Количество ванных комнат не может быть null")
    @Positive(message = "Количество ванных комнат должно быть положительным числом")
    private Integer bathrooms;

    @NotNull(message = "Количество комнат не может быть null")
    @Positive(message = "Количество комнат должно быть положительным числом")
    private Integer rooms;

    @NotNull(message = "Информация о гараже не может быть null")
    private Boolean garage;

    @NotNull(message = "Этаж не может быть null")
    @Positive(message = "Этаж должен быть положительным числом")
    private Integer storey;

    @NotNull(message = "Местоположение не может быть null")
    @NotBlank(message = "Местоположение не может быть пустым")
    private String location;

    @NotNull(message = "Информация о балконе не может быть null")
    private Boolean balcony;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 20, message = "Описание должно содержать минимум 20 символов")
    private String description;

    @NotNull(message = "Доступность не может быть null")
    private Availability availability;

    @NotNull(message = "Площадь не может быть null")
    @Positive(message = "Площадь должна быть положительным числом")
    private Double size;

    @NotNull(message = "Состояние не может быть null")
    private Condition condition;

    @NotNull(message = "Предлагаемая цена не может быть null")
    @Positive(message = "Предлагаемая цена должна быть положительным числом")
    private Double offeredPrice;

}
