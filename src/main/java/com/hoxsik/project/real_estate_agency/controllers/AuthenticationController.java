package com.hoxsik.project.real_estate_agency.controllers;

import com.hoxsik.project.real_estate_agency.dto.request.AuthenticationRequest;
import com.hoxsik.project.real_estate_agency.dto.response.AuthenticationResponse;
import com.hoxsik.project.real_estate_agency.security.DatabaseUserDetails;
import com.hoxsik.project.real_estate_agency.security.DatabaseUserDetailsService;
import com.hoxsik.project.real_estate_agency.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final DatabaseUserDetailsService databaseUserDetailsService;
    private final JwtUtils jwtUtils;

    @PostMapping("/auth/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        DatabaseUserDetails databaseUserDetails = databaseUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        if (Objects.nonNull(databaseUserDetails)) {
            return ResponseEntity.status(HttpStatus.OK).body(new AuthenticationResponse(authenticationRequest.getUsername(), jwtUtils.generateToken(authenticationRequest.getUsername())));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
