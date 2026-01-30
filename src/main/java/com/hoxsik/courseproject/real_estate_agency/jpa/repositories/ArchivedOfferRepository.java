package com.hoxsik.courseproject.real_estate_agency.jpa.repositories;

import com.hoxsik.courseproject.real_estate_agency.dto.response.AgentPercentDto;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Agent;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.ArchivedOffer;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.Condition;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.EstateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArchivedOfferRepository extends JpaRepository<ArchivedOffer, Long> {
    /**
     * Retrieves archived unreviewed customer's transactions
     * @param username Username of the customer
     * @return List of archived offers to review if present, empty otherwise
     */
    @Query("SELECT a FROM ArchivedOffer a " +
            "LEFT JOIN Review r ON r.user = a.customer.user " +
            "WHERE a.customer.user.username = :username " +
            "AND r IS NULL")
    Optional<List<ArchivedOffer>> findCustomersUnreviewedArchivedOffers(@Param("username") String username);

    /**
     * Retrieves archived unreviewed owner's transactions
     * @param username Username of the owner
     * @return List of archived offers to review if present, empty otherwise
     */
    @Query("SELECT a FROM ArchivedOffer a " +
            "LEFT JOIN Review r ON r.user = a.estate.owner.user " +
            "WHERE a.estate.owner.user.username = :username " +
            "AND r IS NULL")
    Optional<List<ArchivedOffer>> findOwnersUnreviewedArchivedOffers(@Param("username") String username);

    /**
     * Retrieves archived owner's transactions
     * @param username Username of the owner
     * @return List of archived offers if present, empty otherwise
     */
    @Query("SELECT a FROM ArchivedOffer a " +
            "WHERE a.estate.owner.user.username = :username")
    Optional<List<ArchivedOffer>> findOwnersArchivedOffers(@Param("username") String username);

    /**
     * Retrieves archived customer's transactions
     * @param username Username of the customer
     * @return List of archived offers if present, empty otherwise
     */
    @Query("SELECT a FROM ArchivedOffer a " +
            "WHERE a.customer.user.username = :username")
    Optional<List<ArchivedOffer>> findCustomersArchivedOffers(@Param("username") String username);

    @Query("""
        SELECT ao
        FROM ArchivedOffer ao
        WHERE ao.estate.availability = 'FOR_SALE'
    """)
    List<ArchivedOffer> findAllForSale();

    @Query("""
        select new com.hoxsik.courseproject.real_estate_agency.dto.response.AgentPercentDto(
            e.agent.user.firstName,
            count(a) * 100.0 / (select count(a2) from ArchivedOffer a2)
        )
        from ArchivedOffer a
        join a.estate e
        group by e.agent.user.firstName
    """)
    List<AgentPercentDto> findArchivedPercents();

    @Query("""
        SELECT a FROM ArchivedOffer a
        WHERE a.estate.type = :type
          AND a.estate.availability = :availability
          AND a.estate.condition = :condition
          AND a.estate.rooms = :rooms
          AND a.estate.size BETWEEN :minSize AND :maxSize
    """)
    List<ArchivedOffer> findAnalogArchivedOffers(
            @Param("type") EstateType type,
            @Param("availability") Availability availability,
            @Param("condition") Condition condition,
            @Param("rooms") int rooms,
            @Param("minSize") double minSize,
            @Param("maxSize") double maxSize
    );
}
