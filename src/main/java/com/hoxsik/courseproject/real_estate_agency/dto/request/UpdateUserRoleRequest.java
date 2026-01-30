package com.hoxsik.courseproject.real_estate_agency.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserRoleRequest {
    @NotBlank
    private String role;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
