package com.hoxsik.project.real_estate_agency.services;

import com.hoxsik.project.real_estate_agency.dto.request.ReviewRequest;
import com.hoxsik.project.real_estate_agency.dto.response.Response;
import com.hoxsik.project.real_estate_agency.jpa.entities.ArchivedOffer;
import com.hoxsik.project.real_estate_agency.jpa.entities.Review;
import com.hoxsik.project.real_estate_agency.jpa.entities.User;
import com.hoxsik.project.real_estate_agency.jpa.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ArchivedOfferService archivedOfferService;

    @Transactional
    public Response reviewTransaction(String username, Long id, ReviewRequest reviewRequest) {
        Optional<User> user = userService.getByUsername(username);

        if (user.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "No account of the provided username found");

        Optional<ArchivedOffer> archivedOffer = archivedOfferService.getByID(id);

        if (archivedOffer.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "No archived offer of the provided ID found");

        reviewRepository.save(createReview(user.get(), archivedOffer.get(), reviewRequest));

        return new Response(true, HttpStatus.CREATED, "Successfully reviewed the transaction");
    }

    public Optional<List<Review>> checkReviewsByAgentID(Long id) {
        return reviewRepository.findByAgentID(id);
    }

    private Review createReview(User user, ArchivedOffer archivedOffer, ReviewRequest reviewRequest)  {
        Review review = new Review();

        review.setUser(user);
        review.setArchivedOffer(archivedOffer);
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setRole(user.getRole());

        return review;
    }
}
