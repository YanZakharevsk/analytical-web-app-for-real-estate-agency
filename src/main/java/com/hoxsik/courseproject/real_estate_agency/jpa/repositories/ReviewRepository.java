package com.hoxsik.courseproject.real_estate_agency.jpa.repositories;

import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * Retrieves reviews by the specified agent ID
     * @param id ID of the agent
     * @return List of reviews if present, empty otherwise
     */
    @Query("SELECT r FROM Review r " +
            "WHERE r.archivedOffer.estate.agent.id = :id")
    Optional<List<Review>> findByAgentID(@Param("id") Long id);
}
