package com.hoxsik.project.real_estate_agency.dto.response;

import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Role;
import lombok.Data;

@Data
public class UserDetailsResponse {
    private Long id;
    private String username;
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
