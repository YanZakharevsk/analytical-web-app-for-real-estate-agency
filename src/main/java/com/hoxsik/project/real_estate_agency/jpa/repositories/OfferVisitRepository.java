package com.hoxsik.project.real_estate_agency.jpa.repositories;

import com.hoxsik.project.real_estate_agency.jpa.entities.OfferVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferVisitRepository extends JpaRepository<OfferVisit, Long> {
}
