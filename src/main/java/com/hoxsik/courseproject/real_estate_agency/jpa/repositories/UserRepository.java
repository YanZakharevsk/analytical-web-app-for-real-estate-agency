package com.hoxsik.courseproject.real_estate_agency.jpa.repositories;

import com.hoxsik.courseproject.real_estate_agency.jpa.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
