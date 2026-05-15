package com.hoxsik.project.real_estate_agency.dto.request;

import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Condition;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.EstateType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstimateRequestDto {
    @NotNull(message = "Тип недвижимости обязателен")
    private EstateType estateType;

    @NotNull(message = "Доступность обязательна")
    private Availability availability;

    @NotNull(message = "Состояние обязательно")
    private Condition condition;

    @NotNull(message = "Число комнат обязательно")
    @Min(value = 1, message = "Комнат должно быть не меньше 1")
    @Max(value = 50, message = "Комнат должно быть не больше 50")
    private Integer rooms;

    @NotNull(message = "Площадь обязательна")
    @DecimalMin(value = "1.0", message = "Площадь должна быть не меньше 1")
    @DecimalMax(value = "100000.0", message = "Площадь слишком велика")
    private Double area;

    @Min(value = 0, message = "Этаж не может быть отрицательным")
    @Max(value = 200, message = "Некорректный этаж")
    private Integer floor;

    @Min(value = 1, message = "Число этажей в доме должно быть не меньше 1")
    @Max(value = 200, message = "Некорректное число этажей")
    private Integer totalFloors;

    @AssertTrue(message = "Для квартиры укажите этаж; для дома — этажность здания")
    public boolean isFloorFieldsConsistent() {
        if (estateType == null) {
            return true;
        }
        if (estateType == EstateType.APARTMENT) {
            return floor != null;
        }
        return totalFloors != null;
    }
}
