package com.hoxsik.courseproject.real_estate_agency.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordUpdateRequest {
    @NotBlank(message = "Старый пароль не может быть пустым")
    @Size(max = 100, message = "Старый пароль не может быть длиннее 100 символов")
    private String oldPassword;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(max = 100, message = "Пароль не может быть длиннее 100 символов")
    private String newPassword;
}
