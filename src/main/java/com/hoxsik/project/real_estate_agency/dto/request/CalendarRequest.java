package com.hoxsik.project.real_estate_agency.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CalendarRequest {
    @NotNull(message = "Слоты не могут быть пустыми")
    private Set<@Future(message = "Слоты должны быть в будущем") LocalDateTime> slots;
}
