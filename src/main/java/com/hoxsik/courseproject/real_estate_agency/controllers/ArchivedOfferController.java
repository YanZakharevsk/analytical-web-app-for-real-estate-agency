package com.hoxsik.courseproject.real_estate_agency.controllers;

import com.hoxsik.courseproject.real_estate_agency.dto.Mapper;
import com.hoxsik.courseproject.real_estate_agency.dto.response.ArchivedOfferResponse;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.ArchivedOffer;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.courseproject.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.courseproject.real_estate_agency.services.ArchivedOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArchivedOfferController {
    private final ArchivedOfferService archivedOfferService;

    @RequiredPrivilege(Privilege.CHECK_ARCHIVED_OFFERS)
    @GetMapping("/customer/archived-offers")
    public ResponseEntity<List<ArchivedOfferResponse>> checkArchivedOffersByCustomer(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<List<ArchivedOffer>> optionalArchivedOffers = archivedOfferService.getCustomerArchivedOffers(userDetails.getUsername());

        return optionalArchivedOffers
                .map(archivedOffers -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(archivedOffers.stream().map(Mapper.INSTANCE::convertArchivedOffer).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @RequiredPrivilege(Privilege.CHECK_ARCHIVED_OFFERS)
    @GetMapping("/owner/archived-offers")
    public ResponseEntity<List<ArchivedOfferResponse>> checkArchivedOffersByOwner(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<List<ArchivedOffer>> optionalArchivedOffers = archivedOfferService.getOwnerArchivedOffers(userDetails.getUsername());

        return optionalArchivedOffers
                .map(archivedOffers -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(archivedOffers.stream().map(Mapper.INSTANCE::convertArchivedOffer).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }
}
