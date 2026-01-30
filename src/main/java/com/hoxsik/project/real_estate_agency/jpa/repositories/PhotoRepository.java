package com.hoxsik.project.real_estate_agency.jpa.repositories;

import com.hoxsik.project.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.project.real_estate_agency.jpa.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Optional<List<Photo>> findByEstate(Estate estate);
}
