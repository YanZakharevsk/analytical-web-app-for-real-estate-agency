package com.hoxsik.project.real_estate_agency.services;

import com.hoxsik.project.real_estate_agency.dto.request.EstateRequest;
import com.hoxsik.project.real_estate_agency.jpa.EstateFilter;
import com.hoxsik.project.real_estate_agency.jpa.entities.Agent;
import com.hoxsik.project.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.project.real_estate_agency.jpa.entities.Owner;
import com.hoxsik.project.real_estate_agency.jpa.repositories.EstateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstateService {
    private final EstateRepository estateRepository;
    private final AgentService agentService;
    private final OwnerService ownerService;

    @Transactional
    public Optional<Long> reportEstate(String username, EstateRequest estateRequest) {
        Optional<Owner> owner = ownerService.getByUsername(username);
        Optional<Agent> agent = agentService.getByID(estateRequest.getAgent());

        if (owner.isEmpty() || agent.isEmpty())
            return Optional.empty();

        Estate estate = createEstate(owner.get(), agent.get(), estateRequest);

        return  Optional.of(estateRepository.save(estate).getId());
    }

    public Optional<List<Estate>> getReportedEstatesByAgentUsername(String username) {
        Optional<Agent> agent = agentService.getByUsername(username);

        if (agent.isEmpty())
            return Optional.empty();

        return estateRepository.findReportedEstatesByAgent(agent.get());
    }

    public Optional<List<Estate>> getAllEstates() {
        List<Estate> estates = estateRepository.findAll();

        if (estates.isEmpty())
            return Optional.empty();

        return Optional.of(estates);
    }

    public Optional<Estate> getByID(Long id) {
        return estateRepository.findById(id);
    }

    public Optional<List<Estate>> getFilteredEstates(Integer bathrooms, Integer rooms, Boolean garage, Integer storey,
                                                     String location, Boolean balcony, Double size, String condition,
                                                     String type, String availability, Double priceFrom, Double priceTo,
                                                     LocalDateTime postFrom, LocalDateTime postTo) {

        return estateRepository.findAll(EstateFilter.filterEstates(type, bathrooms, rooms, garage, storey, location, balcony,
                availability, size, condition, priceFrom, priceTo, postFrom, postTo));
    }

    public Optional<List<Estate>> getByOwnerUsername(String username) {
        return estateRepository.findByOwnerUsername(username);
    }

    private Estate createEstate(Owner owner, Agent agent, EstateRequest estateRequest) {
        Estate estate = new Estate();

        estate.setOwner(owner);
        estate.setAgent(agent);
        estate.setType(estateRequest.getType());
        estate.setBathrooms(estateRequest.getBathrooms());
        estate.setRooms(estateRequest.getRooms());
        estate.setGarage(estateRequest.getGarage());
        estate.setStorey(estateRequest.getStorey());
        estate.setLocation(estateRequest.getLocation());
        estate.setBalcony(estateRequest.getBalcony());
        estate.setDescription(estateRequest.getDescription());
        estate.setAvailability(estateRequest.getAvailability());
        estate.setSize(estateRequest.getSize());
        estate.setCondition(estateRequest.getCondition());
        estate.setOfferedPrice(estateRequest.getOfferedPrice());

        return estate;
    }

    @Transactional
    public boolean deleteEstateByOwner(String username, Long estateId) {
        Optional<Estate> estateOpt = estateRepository.findById(estateId);

        if (estateOpt.isEmpty())
            return false;

        Estate estate = estateOpt.get();

        if (!estate.getOwner().getUser().getUsername().equals(username))
            return false;

        if (estate.getIsSubmitted())
            return false;

        estateRepository.delete(estate);
        return true;
    }
}
