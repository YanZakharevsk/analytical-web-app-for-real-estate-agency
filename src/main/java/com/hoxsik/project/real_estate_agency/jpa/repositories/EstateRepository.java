package com.hoxsik.project.real_estate_agency.jpa.repositories;

import com.hoxsik.project.real_estate_agency.jpa.entities.Agent;
import com.hoxsik.project.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.EstateType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstateRepository extends JpaRepository<Estate, Long> {
    /**
     * Retrieves all estates assigned to the agent
     * @param agent Agent to whom estates are assigned
     * @return List of estates if are present, empty otherwise
     */
    Optional<List<Estate>> findByAgent(Agent agent);

    /**
     * Retrieves all the reported estates to which documents and photos are attached
     * @param agent Agent to whom estates are assigned
     * @return List of estates if are present, empty otherwise
     */
    @Query("""
    SELECT e FROM Estate e
    JOIN Document d ON d.estate = e
    JOIN Photo p ON p.estate = e
    LEFT JOIN Offer o ON o.estate = e
    LEFT JOIN ArchivedOffer ao ON ao.estate = e
    WHERE e.agent = :agent
      AND o IS NULL
      AND ao IS NULL
""")
    Optional<List<Estate>> findReportedEstatesByAgent(@Param("agent") Agent agent);


    /**
     * Retrieves all the estates that match specified predicates
     * @param specification Specification containing predicates
     * @return List of estates if are present, empty otherwise
     */
    Optional<List<Estate>> findAll(Specification<Estate> specification);

    /**
     * Retrieves all the estates for the specified owner
     * @param username Username of the owner
     * @return List of estates if present, empty otherwise
     */
    @Query("SELECT e FROM Estate e " +
            "WHERE e.owner.user.username = :username")
    Optional<List<Estate>> findByOwnerUsername(@Param("username") String username);

    @Query("""
        SELECT e FROM Estate e
        WHERE e.type = :type
          AND e.availability = :availability
          AND e.postDate IS NOT NULL
    """)
    List<Estate> findApartmentsForSale(
            @Param("type") EstateType type,
            @Param("availability") Availability availability
    );

    @Query("""
    select e
    from Estate e
    where e.availability = com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.Availability.FOR_RENT
""")
    List<Estate> findAllForRent();



}
