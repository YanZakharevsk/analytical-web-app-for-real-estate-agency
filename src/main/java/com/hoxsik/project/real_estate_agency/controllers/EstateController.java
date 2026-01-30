package com.hoxsik.project.real_estate_agency.controllers;

import com.hoxsik.project.real_estate_agency.dto.Mapper;
import com.hoxsik.project.real_estate_agency.dto.request.EstateRequest;
import com.hoxsik.project.real_estate_agency.dto.response.EstateResponse;
import com.hoxsik.project.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.project.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.project.real_estate_agency.services.EstateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EstateController {
    private final EstateService estateService;

    @RequiredPrivilege(Privilege.REPORT_OFFER)
    @PostMapping("/owner/report-offer")
    public ResponseEntity<Long> reportOffer(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody EstateRequest estateRequest) {
        Optional<Long> id = estateService.reportEstate(userDetails.getUsername(), estateRequest);
        return id.map(aLong -> ResponseEntity.status(HttpStatus.CREATED).body(aLong)).orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @RequiredPrivilege(Privilege.CHECK_REPORTED_ESTATES)
    @GetMapping("/agent/reported-estates")
    public ResponseEntity<List<EstateResponse>> checkReportedEstates(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<List<Estate>> estates = estateService.getReportedEstatesByAgentUsername(userDetails.getUsername());

        return estates
                .map(estateList -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(estateList.stream().map(Mapper.INSTANCE::convertEstate).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @RequiredPrivilege(Privilege.REPORT_OFFER)
    @GetMapping("/owner/my-estates")
    public ResponseEntity<List<EstateResponse>> checkMyEstates(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<List<Estate>> optionalEstates = estateService.getByOwnerUsername(userDetails.getUsername());

        return optionalEstates.map(estates -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(estates.stream().map(Mapper.INSTANCE::convertEstate).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @RequiredPrivilege(Privilege.REPORT_OFFER)
    @DeleteMapping("/owner/delete-estate/{id}")
    public ResponseEntity<Void> deleteEstate(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        boolean deleted = estateService.deleteEstateByOwner(userDetails.getUsername(), id);

        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
