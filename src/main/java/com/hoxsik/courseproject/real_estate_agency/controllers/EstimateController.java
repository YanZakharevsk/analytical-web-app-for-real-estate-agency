package com.hoxsik.courseproject.real_estate_agency.controllers;

import com.hoxsik.courseproject.real_estate_agency.dto.request.EstimateRequestDto;
import com.hoxsik.courseproject.real_estate_agency.dto.response.EstimateResponseDto;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.courseproject.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.courseproject.real_estate_agency.services.EstimateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estimate")
@RequiredArgsConstructor
public class EstimateController {
    private final EstimateService estimateService;

    @RequiredPrivilege(Privilege.CHECK_ANALYTICS)
    @PostMapping
    public EstimateResponseDto estimate(
            @AuthenticationPrincipal UserDetails userDetails, // Получаем залогиненого пользователя
            @RequestBody EstimateRequestDto dto
    ) {
        if (userDetails == null) {
            throw new RuntimeException("Пользователь не авторизован");
        }

        return estimateService.estimate(dto);
    }
}
