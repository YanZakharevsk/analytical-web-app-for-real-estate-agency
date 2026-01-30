package com.hoxsik.project.real_estate_agency.controllers;

import com.hoxsik.project.real_estate_agency.dto.request.UpdateUserRoleRequest;
import com.hoxsik.project.real_estate_agency.dto.request.UserRequest;
import com.hoxsik.project.real_estate_agency.dto.response.Response;
import com.hoxsik.project.real_estate_agency.dto.response.UserResponse;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.project.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.project.real_estate_agency.services.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @RequiredPrivilege(Privilege.ADD_ADMIN)
    @PostMapping("/admin/add-admin")
    public ResponseEntity<Response> createAdminAccount(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UserRequest userRequest){
        Response response = adminService.createAdminAccount(userRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @RequiredPrivilege(Privilege.VIEW_USERS)
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @RequiredPrivilege(Privilege.UPDATE_USER_ROLE)
    @PostMapping("/admin/users/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable Long id, @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(adminService.updateUserRole(id, request));
    }

    @RequiredPrivilege(Privilege.REMOVE_USER)
    @PostMapping("/admin/users/{id}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
