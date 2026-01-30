package com.hoxsik.courseproject.real_estate_agency.services;

import com.hoxsik.courseproject.real_estate_agency.dto.request.UpdateUserRoleRequest;
import com.hoxsik.courseproject.real_estate_agency.dto.request.UserRequest;
import com.hoxsik.courseproject.real_estate_agency.dto.response.Response;
import com.hoxsik.courseproject.real_estate_agency.dto.response.UserResponse;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Admin;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.User;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.Role;
import com.hoxsik.courseproject.real_estate_agency.jpa.repositories.AdminRepository;
import com.hoxsik.courseproject.real_estate_agency.jpa.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public Response createAdminAccount(UserRequest userRequest){
        Response response = userService.createUserAccount(userRequest, Role.ADMIN);

        if(!response.isSuccess())
            return response;

        Admin admin = new Admin();
        admin.setUser(userService.getByUsername(userRequest.getUsername()).get());

        adminRepository.save(admin);

        return new Response(true, HttpStatus.CREATED, "Успешное создание админ аккаунта");
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getRole().name()))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUserRole(Long userId, UpdateUserRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Role newRole;
        try {
            newRole = Role.valueOf(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Неверная роль");
        }

        user.setRole(newRole);
        userRepository.save(user);

        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name());
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
