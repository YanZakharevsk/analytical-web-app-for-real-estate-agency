package com.hoxsik.courseproject.real_estate_agency.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 100, message = "Имя не может быть длиннее 100 символов")
    private String firstName;

    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(max = 100, message = "Фамилия не может быть длиннее 100 символов")
    private String lastName;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(max = 50, min = 5, message = "Имя пользователя не может быть длиннее 50 или короче 5 символов")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(max = 100, message = "Пароль не может быть длиннее 100 символов")
    private String password;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    @Size(max = 50, message = "Email не может быть длиннее 50 символов")
    private String email;

    @NotBlank(message = "Номер телефона не может быть пустым")
    @Pattern(
            regexp = "\\+375\\d{9}",
            message = "Телефон должен быть в формате +375XXXXXXXXX"
    )    private String phoneNumber;
}
