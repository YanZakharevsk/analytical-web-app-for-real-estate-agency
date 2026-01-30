package com.hoxsik.courseproject.real_estate_agency.dto.response;

public class UserResponse {
    private Long id;
    private String username;
    private String role;

    public UserResponse(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    // геттеры и сеттеры
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}
