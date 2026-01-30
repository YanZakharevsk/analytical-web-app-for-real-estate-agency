package com.hoxsik.courseproject.real_estate_agency.controllers;

import com.hoxsik.courseproject.real_estate_agency.dto.Mapper;
import com.hoxsik.courseproject.real_estate_agency.dto.request.CredentialsUpdateRequest;
import com.hoxsik.courseproject.real_estate_agency.dto.request.PasswordUpdateRequest;
import com.hoxsik.courseproject.real_estate_agency.dto.response.Response;
import com.hoxsik.courseproject.real_estate_agency.dto.response.UserDetailsResponse;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.courseproject.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.courseproject.real_estate_agency.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RequiredPrivilege(Privilege.CHANGE_CREDENTIALS)
    @PatchMapping("/update-credentials")
    public ResponseEntity<Response> updateCredentials(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CredentialsUpdateRequest credentialsUpdateRequest) {
        Response response = userService.updateCredentials(userDetails.getUsername(), credentialsUpdateRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @RequiredPrivilege(Privilege.CHANGE_PASSWORD)
    @PatchMapping("/update-password")
    public ResponseEntity<Response> updatePassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        Response response = userService.updatePassword(userDetails.getUsername(), passwordUpdateRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @RequiredPrivilege(Privilege.CHECK_DETAILS)
    @GetMapping("/user-details")
    public ResponseEntity<UserDetailsResponse> checkUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getByUsername(userDetails.getUsername())
                .map(user -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Mapper.INSTANCE.convertUserDetails(user)))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }
}
