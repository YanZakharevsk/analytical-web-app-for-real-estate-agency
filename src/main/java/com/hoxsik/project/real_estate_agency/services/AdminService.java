package com.hoxsik.project.real_estate_agency.services;

import com.hoxsik.project.real_estate_agency.dto.request.AdminUserUpdateRequest;
import com.hoxsik.project.real_estate_agency.dto.request.UpdateUserRoleRequest;
import com.hoxsik.project.real_estate_agency.dto.request.UserRequest;
import com.hoxsik.project.real_estate_agency.dto.response.Response;
import com.hoxsik.project.real_estate_agency.dto.response.UserResponse;
import com.hoxsik.project.real_estate_agency.jpa.entities.Admin;
import com.hoxsik.project.real_estate_agency.jpa.entities.Agent;
import com.hoxsik.project.real_estate_agency.jpa.entities.ArchivedOffer;
import com.hoxsik.project.real_estate_agency.jpa.entities.Customer;
import com.hoxsik.project.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.project.real_estate_agency.jpa.entities.Owner;
import com.hoxsik.project.real_estate_agency.jpa.entities.User;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Role;
import com.hoxsik.project.real_estate_agency.jpa.repositories.AdminRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.AgentRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.ArchivedOfferRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.CalendarRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.CustomerRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.DocumentRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.EstateRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.MeetingRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.OfferRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.OwnerRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.PhotoRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.ReviewRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final MeetingRepository meetingRepository;
    private final CalendarRepository calendarRepository;
    private final EstateRepository estateRepository;
    private final OfferRepository offerRepository;
    private final PhotoRepository photoRepository;
    private final DocumentRepository documentRepository;
    private final ArchivedOfferRepository archivedOfferRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public Response createAdminAccount(UserRequest userRequest) {
        Response response = userService.createUserAccount(userRequest, Role.ADMIN);

        if (!response.isSuccess()) {
            return response;
        }

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
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Редактирование учётной записи администратора запрещено");
        }

        Role newRole;
        try {
            newRole = Role.valueOf(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверная роль");
        }
        if (newRole == Role.ADMIN) {
            throw new IllegalArgumentException("Нельзя назначить роль администратора");
        }

        applyRoleChange(user, newRole);

        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name());
    }

    /**
     * Частичное обновление пользователя администратором (логин и/или роль).
     */
    @Transactional
    public UserResponse updateUser(Long userId, AdminUserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Редактирование учётной записи администратора запрещено");
        }

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            String newUsername = request.getUsername().trim();
            if (userService.isUsernameTakenBySomeoneElse(newUsername, userId)) {
                throw new IllegalArgumentException("Логин уже занят");
            }
            user.setUsername(newUsername);
        }

        Role newRole = null;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                newRole = Role.valueOf(request.getRole().trim());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Неверная роль");
            }
            if (newRole == Role.ADMIN) {
                throw new IllegalArgumentException("Нельзя назначить роль администратора");
            }
        }

        if (newRole != null && newRole != user.getRole()) {
            applyRoleChange(user, newRole);
        } else {
            userRepository.save(user);
        }

        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name());
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Нельзя удалить учётную запись администратора");
        }

        meetingRepository.deleteByUser_Id(user.getId());
        removeProfileForRole(user.getId(), user.getRole(), true);
        userRepository.delete(user);
    }

    private void applyRoleChange(User user, Role newRole) {
        Role oldRole = user.getRole();
        if (oldRole == newRole) {
            return;
        }
        removeProfileForRole(user.getId(), oldRole, false);
        user.setRole(newRole);
        userRepository.saveAndFlush(user);

        createProfileForRole(user, newRole);
    }

    private void removeProfileForRole(Long userId, Role oldRole, boolean fullUserDeletion) {
        switch (oldRole) {
            case AGENT -> removeAgentProfile(userId);
            case CUSTOMER -> removeCustomerProfile(userId);
            case OWNER -> removeOwnerProfile(userId, fullUserDeletion);
            default -> {
            }
        }
    }

    private void removeAgentProfile(Long userId) {
        agentRepository.findByUser_Id(userId).ifPresent(agent -> {
            meetingRepository.deleteByAgent_Id(agent.getId());
            calendarRepository.deleteByAgent_Id(agent.getId());
            estateRepository.findByAgent(agent).ifPresent(estates -> estates.forEach(estate -> {
                estate.setAgent(null);
                estateRepository.save(estate);
            }));
            agentRepository.delete(agent);
        });
    }

    private void removeCustomerProfile(Long userId) {
        customerRepository.findByUser_Id(userId).ifPresent(customer -> {
            unlinkCustomerFromOffers(customer);
            for (ArchivedOffer ao : new ArrayList<>(archivedOfferRepository.findByCustomer_Id(customer.getId()))) {
                reviewRepository.findByArchivedOffer_Id(ao.getId()).ifPresent(reviewRepository::delete);
                archivedOfferRepository.delete(ao);
            }
            customerRepository.delete(customer);
        });
    }

    private void unlinkCustomerFromOffers(Customer customer) {
        for (var offer : new ArrayList<>(offerRepository.findByCustomers_Id(customer.getId()))) {
            offer.getCustomers().remove(customer);
            offerRepository.save(offer);
        }
        for (var offer : offerRepository.findByBlockedBy_Id(customer.getId())) {
            offer.setBlockedBy(null);
            offer.setBlocked(false);
            offerRepository.save(offer);
        }
    }

    private void removeOwnerProfile(Long userId, boolean fullUserDeletion) {
        Optional<Owner> ownerOpt = fullUserDeletion
                ? ownerRepository.findByUser_IdWithEstates(userId)
                : ownerRepository.findByUser_Id(userId);
        if (ownerOpt.isEmpty()) {
            return;
        }
        Owner owner = ownerOpt.get();
        if (!fullUserDeletion && owner.getEstates() != null && !owner.getEstates().isEmpty()) {
            throw new IllegalArgumentException("Нельзя сменить роль владельца с привязанными объектами недвижимости");
        }
        if (fullUserDeletion) {
            deleteOwnerEstatesCascade(owner);
        }
        ownerRepository.delete(owner);
    }

    private void deleteOwnerEstatesCascade(Owner owner) {
        if (owner.getEstates() == null) {
            return;
        }
        for (Estate estate : new ArrayList<>(owner.getEstates())) {
            deleteEstateGraph(estate);
        }
    }

    private void deleteEstateGraph(Estate estate) {
        if (estate.getOffer() != null) {
            offerRepository.delete(estate.getOffer());
        }
        if (estate.getArchivedOffer() != null) {
            ArchivedOffer ao = estate.getArchivedOffer();
            reviewRepository.findByArchivedOffer_Id(ao.getId()).ifPresent(reviewRepository::delete);
            archivedOfferRepository.delete(ao);
        }
        photoRepository.findByEstate(estate).ifPresent(list -> photoRepository.deleteAll(list));
        documentRepository.findByEstate_Id(estate.getId()).forEach(documentRepository::delete);
        estateRepository.delete(estate);
    }

    private void createProfileForRole(User user, Role role) {
        switch (role) {
            case AGENT -> {
                if (agentRepository.findByUser_Id(user.getId()).isEmpty()) {
                    Agent agent = new Agent();
                    agent.setUser(user);
                    agentRepository.save(agent);
                }
            }
            case CUSTOMER -> {
                if (customerRepository.findByUser_Id(user.getId()).isEmpty()) {
                    Customer customer = new Customer();
                    customer.setUser(user);
                    customerRepository.save(customer);
                }
            }
            case OWNER -> {
                if (ownerRepository.findByUser_Id(user.getId()).isEmpty()) {
                    Owner owner = new Owner();
                    owner.setUser(user);
                    ownerRepository.save(owner);
                }
            }
            default -> {
            }
        }
    }
}
