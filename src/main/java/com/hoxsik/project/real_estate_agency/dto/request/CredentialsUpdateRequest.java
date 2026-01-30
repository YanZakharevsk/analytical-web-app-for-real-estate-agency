package com.hoxsik.project.real_estate_agency.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CredentialsUpdateRequest {
    @Size(max = 100, message = "Имя не может быть длиннее 100 символов")
    private String firstName;

    @Size(max = 100, message = "Фамилия не может быть длиннее 100 символов")
    private String lastName;

    //@Size(max = 50, min = 5, message = "Username cannot be longer than 50 or shorter than 5 characters")
    private String username;

    @Email(message = "Неверный формат email-адреса")
    @Size(max = 50, message = "Email-адрес не может быть длиннее 50 символов")
    private String email;

    @Pattern(
            regexp = "\\+375\\d{9}",
            message = "Телефон должен быть в формате +375XXXXXXXXX"
    )    private String phoneNumber;
}
