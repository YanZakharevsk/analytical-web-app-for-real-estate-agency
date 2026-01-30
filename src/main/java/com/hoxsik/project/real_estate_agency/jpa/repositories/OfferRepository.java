package com.hoxsik.project.real_estate_agency.jpa.repositories;

import com.hoxsik.project.real_estate_agency.dto.response.AgentPercentDto;
import com.hoxsik.project.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.project.real_estate_agency.jpa.entities.Offer;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Condition;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.EstateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    /**
     * Retrieves the offer by the specified estate
     * @param estate Estate which is included in the offer
     * @return Offer object
     */
    Offer findByEstate(Estate estate);

    /**
     * Retrieves blocked offers by assigned agent
     * @param username Username of the agent assigned to the offer
     * @return List of blocked offers if present, empty otherwise
     */
    @Query("SELECT o FROM Offer o " +
            "WHERE o.blocked = TRUE " +
            "AND o.estate.agent.user.username = :username")
    Optional<List<Offer>> findBlockedOffersByAgentUsername(@Param("username") String username);

    /**
     * Retrieves blocked offers by the specified customer
     * @param username Username of the customer whose blocked offers are retrieved
     * @return List of blocked offers if present, empty otherwise
     */
    @Query("SELECT o FROM Offer o " +
            "WHERE o.blocked = TRUE " +
            "AND o.blockedBy.user.username = :username")
    Optional<List<Offer>> findBlockedOffersByCustomerUsername(@Param("username") String username);

    @Query("""
        SELECT o
        FROM Offer o
        WHERE o.estate.availability = 'FOR_SALE'
    """)
    List<Offer> findAllForSale();

    @Query("""
        select new com.hoxsik.courseproject.real_estate_agency.dto.response.AgentPercentDto(
            e.agent.user.firstName,
            count(o) * 100.0 / (select count(o2) from Offer o2)
        )
        from Offer o
        join o.estate e
        group by e.agent.user.firstName
    """)
    List<AgentPercentDto> findOfferPercents();

    @Query("""
        SELECT o FROM Offer o
        WHERE o.estate.type = :type
          AND o.estate.availability = :availability
          AND o.estate.condition = :condition
          AND o.estate.rooms = :rooms
          AND o.estate.size BETWEEN :minSize AND :maxSize
    """)
    List<Offer> findAnalogOffers(
            @Param("type") EstateType type,
            @Param("availability") Availability availability,
            @Param("condition") Condition condition,
            @Param("rooms") int rooms,
            @Param("minSize") double minSize,
            @Param("maxSize") double maxSize
    );
}
