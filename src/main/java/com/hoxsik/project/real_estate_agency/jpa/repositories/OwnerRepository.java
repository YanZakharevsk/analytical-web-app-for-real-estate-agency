package com.hoxsik.project.real_estate_agency.jpa.repositories;

import com.hoxsik.project.real_estate_agency.jpa.entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByUser_Id(Long userId);

    @Query("SELECT DISTINCT o FROM Owner o LEFT JOIN FETCH o.estates WHERE o.user.id = :userId")
    Optional<Owner> findByUser_IdWithEstates(@Param("userId") Long userId);

    @Query( "SELECT o FROM Owner o " +
            "JOIN User u ON u.id = o.user.id " +
            "WHERE u.username = :username")
    Optional<Owner> findByUsername(@Param("username") String username);
}
