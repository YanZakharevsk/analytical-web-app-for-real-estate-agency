package com.hoxsik.courseproject.real_estate_agency.controllers;

import com.hoxsik.courseproject.real_estate_agency.dto.Mapper;
import com.hoxsik.courseproject.real_estate_agency.dto.request.ReviewRequest;
import com.hoxsik.courseproject.real_estate_agency.dto.response.Response;
import com.hoxsik.courseproject.real_estate_agency.dto.response.ReviewResponse;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Review;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.courseproject.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.courseproject.real_estate_agency.services.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class ReviewController {
    private final ReviewService reviewService;

    @RequiredPrivilege(Privilege.REVIEW_AGENT)
    @PostMapping("/review")
    public ResponseEntity<Response> reviewAgent(@AuthenticationPrincipal UserDetails userDetails, @NotNull @RequestParam("id") Long id, @Valid @RequestBody ReviewRequest reviewRequest) {
        Response response = reviewService.reviewTransaction(userDetails.getUsername(), id, reviewRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/auth/reviews")
    public ResponseEntity<List<ReviewResponse>> checkReviews(@NotNull @RequestParam("id") Long id) {
        Optional<List<Review>> optionalReviews = reviewService.checkReviewsByAgentID(id);

        return optionalReviews
                .map(reviews -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(reviews.stream().map(Mapper.INSTANCE::convertReview).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

}
