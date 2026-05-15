package com.hoxsik.project.real_estate_agency.dto.response;

import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String username;
    private String token;
    private String role;
}
