package com.hoxsik.project.real_estate_agency.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUserUpdateRequest {

    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    private String username;

    @Pattern(regexp = "CUSTOMER|OWNER|AGENT", message = "Роль должна быть CUSTOMER, OWNER или AGENT")
    private String role;

    @AssertTrue(message = "Укажите новый логин и/или роль")
    public boolean isAtLeastOneField() {
        boolean hasUsername = username != null && !username.isBlank();
        boolean hasRole = role != null && !role.isBlank();
        return hasUsername || hasRole;
    }
}
