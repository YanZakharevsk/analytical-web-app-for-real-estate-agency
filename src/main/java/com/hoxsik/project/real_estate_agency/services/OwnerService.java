package com.hoxsik.project.real_estate_agency.services;

import com.hoxsik.project.real_estate_agency.dto.request.UserRequest;
import com.hoxsik.project.real_estate_agency.dto.response.Response;
import com.hoxsik.project.real_estate_agency.jpa.entities.Owner;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Role;
import com.hoxsik.project.real_estate_agency.jpa.repositories.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final UserService userService;

    @Transactional
    public Response createOwnerAccount(UserRequest userRequest) {
        Response response = userService.createUserAccount(userRequest, Role.OWNER);

        if (!response.isSuccess())
            return response;

        Owner owner = new Owner();
        owner.setUser(userService.getByUsername(userRequest.getUsername()).get());

        ownerRepository.save(owner);

        return new Response(true, HttpStatus.CREATED, "Успешное создание аккаунта владельца");
    }

    public Optional<Owner> getByUsername(String username) {
        return ownerRepository.findByUsername(username);
    }

    public Optional<Owner> getByID(Long id) {
        return ownerRepository.findById(id);
    }
}
