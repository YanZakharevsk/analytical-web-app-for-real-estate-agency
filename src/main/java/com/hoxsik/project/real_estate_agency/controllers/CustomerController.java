package com.hoxsik.project.real_estate_agency.controllers;

import com.hoxsik.project.real_estate_agency.dto.request.UserRequest;
import com.hoxsik.project.real_estate_agency.dto.response.Response;
import com.hoxsik.project.real_estate_agency.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/auth/customer/register")
    public ResponseEntity<Response> registerCustomer(@Valid @RequestBody UserRequest userRequest) {
        Response response = customerService.createCustomerAccount(userRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
