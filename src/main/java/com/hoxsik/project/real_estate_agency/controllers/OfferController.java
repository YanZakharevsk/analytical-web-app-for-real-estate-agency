package com.hoxsik.project.real_estate_agency.controllers;

import com.hoxsik.project.real_estate_agency.dto.Mapper;
import com.hoxsik.project.real_estate_agency.dto.request.OfferRequest;
import com.hoxsik.project.real_estate_agency.dto.response.OfferPreviewResponse;
import com.hoxsik.project.real_estate_agency.dto.response.OfferResponse;
import com.hoxsik.project.real_estate_agency.dto.response.Response;
import com.hoxsik.project.real_estate_agency.jpa.entities.Offer;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.project.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.project.real_estate_agency.services.OfferService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OfferController {
    private final OfferService offerService;

    @RequiredPrivilege(Privilege.ADD_OFFER)
    @PostMapping("/agent/post-offer")
    public ResponseEntity<Response> postOffer(@AuthenticationPrincipal UserDetails userDetails, @Param("id") Long id, @Valid @RequestBody OfferRequest offerRequest) {
        Response response = offerService.postOffer(id, offerRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @RequiredPrivilege(Privilege.CHECK_OFFERS)
    @GetMapping("/auth/offers")
    public ResponseEntity<List<OfferPreviewResponse>> filterEstates(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "bathrooms", required = false) Integer bathrooms,
            @RequestParam(value = "rooms", required = false) Integer rooms,
            @RequestParam(value = "garage", required = false) Boolean garage,
            @RequestParam(value = "storey", required = false) Integer storey,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "balcony", required = false) Boolean balcony,
            @RequestParam(value = "availability", required = false) String availability,
            @RequestParam(value = "size", required = false) Double size,
            @RequestParam(value = "condition", required = false) String condition,
            @RequestParam(value = "priceFrom", required = false) Double priceFrom,
            @RequestParam(value = "priceTo", required = false) Double priceTo,
            @RequestParam(value = "postFrom", required = false) LocalDateTime postFrom,
            @RequestParam(value = "postTo", required = false) LocalDateTime postTo
    ) {
        Optional<List<Offer>> optionalOffers = offerService.getFilteredOffers(
                bathrooms, rooms, garage, storey, location, balcony, size,
                condition, type, availability, priceFrom, priceTo, postFrom, postTo);

        return optionalOffers
                .map(estates -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(estates.stream().map(Mapper.INSTANCE::convertOfferPreview).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @RequiredPrivilege(Privilege.BLOCK_OFFER)
    @PatchMapping("/customer/block-offer")
    public ResponseEntity<Response> blockOffer(@AuthenticationPrincipal UserDetails userDetails, @NotNull @RequestParam("id") Long id) {
        Response response = offerService.blockOffer(userDetails.getUsername(), id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @RequiredPrivilege(Privilege.UNBLOCK_OFFER)
    @PatchMapping("/unblock-offer")
    public ResponseEntity<Response> unblockOffer(@AuthenticationPrincipal UserDetails userDetails, @NotNull @RequestParam("id") Long id) {
        Response response = offerService.unblockOffer(userDetails.getUsername(), id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @RequiredPrivilege(Privilege.CHECK_BLOCKED_OFFERS)
    @GetMapping("/agent/to-finalize")
    public ResponseEntity<List<OfferResponse>> checkOffersToFinalize(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<List<Offer>> optionalOffers = offerService.getBlockedOffersByAgentUsername(userDetails.getUsername());

        return optionalOffers
                .map(offers -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(offers.stream().map(Mapper.INSTANCE::convertOffer).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @RequiredPrivilege(Privilege.CHECK_BLOCKED_OFFERS)
    @GetMapping("/customer/blocked-offers")
    public ResponseEntity<List<OfferResponse>> checkMyBlockedOffers(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<List<Offer>> optionalOffers = offerService.getBlockedOffersByCustomerUsername(userDetails.getUsername());

        return optionalOffers
                .map(offers -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(offers.stream().map(Mapper.INSTANCE::convertOffer).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @RequiredPrivilege(Privilege.FINALIZE_OFFER)
    @PostMapping("/agent/finalize-offer")
    public ResponseEntity<Response> finalizeOffer(@AuthenticationPrincipal UserDetails userDetails, @NotNull @RequestParam("id") Long id) {
        Response response = offerService.finalizeOffer(userDetails.getUsername(), id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @RequiredPrivilege(Privilege.ADD_TO_FAVORITES)
    @PostMapping("/customer/add-to-favorites")
    public ResponseEntity<Response> addToFavorites(@AuthenticationPrincipal UserDetails userDetails, @NotNull @RequestParam("id") Long id) {
        Response response = offerService.addOfferToFavorites(userDetails.getUsername(), id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @RequiredPrivilege(Privilege.CHECK_FAVORITES)
    @GetMapping("/customer/favorites")
    public ResponseEntity<List<OfferResponse>> checkFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<List<Offer>> optionalOffers = offerService.getFavoriteOffers(userDetails.getUsername());

        return optionalOffers
                .map(offers -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(offers.stream().map(Mapper.INSTANCE::convertOffer).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @RequiredPrivilege(Privilege.REMOVE_FROM_FAVORITES)
    @PostMapping("/customer/remove-from-favorites")
    public ResponseEntity<Response> removeFromFavorites(@AuthenticationPrincipal UserDetails userDetails, @NotNull @RequestParam("id") Long id) {
        Response response = offerService.removeOfferFromFavorites(userDetails.getUsername(), id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @RequiredPrivilege(Privilege.CHECK_OFFER_DETAILS)
    @GetMapping("/customer/offer")
    public ResponseEntity<OfferResponse> checkOffer(@AuthenticationPrincipal UserDetails userDetails, @NotNull @RequestParam("id") Long id) {
        Optional<Offer> optionalOffer = offerService.checkOffer(userDetails.getUsername(), id);

        return optionalOffer
                .map(offer -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Mapper.INSTANCE.convertOffer(offer)))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }
}
