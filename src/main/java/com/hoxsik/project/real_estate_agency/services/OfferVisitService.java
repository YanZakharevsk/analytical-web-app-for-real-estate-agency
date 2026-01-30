package com.hoxsik.project.real_estate_agency.services;

import com.hoxsik.project.real_estate_agency.jpa.entities.Customer;
import com.hoxsik.project.real_estate_agency.jpa.entities.Offer;
import com.hoxsik.project.real_estate_agency.jpa.entities.OfferVisit;
import com.hoxsik.project.real_estate_agency.jpa.repositories.OfferVisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferVisitService {
    private final OfferVisitRepository offerVisitRepository;

    @Transactional
    public void addOfferVisit(Customer customer, Offer offer) {
        OfferVisit offerVisit = new OfferVisit();

        offerVisit.setOffer(offer);
        offerVisit.setCustomer(customer);

        offerVisitRepository.save(offerVisit);
    }
}
