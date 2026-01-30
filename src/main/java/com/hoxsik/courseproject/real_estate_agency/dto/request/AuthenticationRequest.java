package com.hoxsik.courseproject.real_estate_agency.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class AuthenticationRequest {
    @NotNull(message = "Логин не может быть нулевым")
    @NotBlank(message = "Логин не может быть пустым")
    private String username;

    @NotNull(message = "Пароль не может быть нулевым")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
