package com.hoxsik.project.real_estate_agency.services;

import com.hoxsik.project.real_estate_agency.dto.request.UserRequest;
import com.hoxsik.project.real_estate_agency.dto.response.Response;
import com.hoxsik.project.real_estate_agency.jpa.entities.Agent;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Role;
import com.hoxsik.project.real_estate_agency.jpa.repositories.AgentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AgentRepository agentRepository;
    private final UserService userService;

    /**
     * Allows to create an Agent account
     * @param userRequest Details of the agent whose account is created
     * @return Response if successfully created the account
     */
    @Transactional
    public Response createAgentAccount(UserRequest userRequest){
        Response response = userService.createUserAccount(userRequest, Role.AGENT);

        if(!response.isSuccess())
            return response;

        Agent agent = new Agent();
        agent.setUser(userService.getByUsername(userRequest.getUsername()).get());

        agentRepository.save(agent);

        return new Response(true, HttpStatus.CREATED, "Успешное создание менеджер аккаунта");
    }

    /**
     * Retrieves an agent by their username
     * @param username Username of the agent
     * @return Agent object if present, empty otherwise
     */
    public Optional<Agent> getByUsername(String username) {
        return agentRepository.findByUsername(username);
    }

    /**
     * Retrieves all the agents
     * @return List of agents if present, empty otherwise
     */
    public Optional<List<Agent>> getAllAgents() {
        List<Agent> agents = agentRepository.findAll();

        if (agents.isEmpty())
            return Optional.empty();

        return Optional.of(agents);
    }

    /**
     * Retrieves an agent by their ID
     * @param id ID of the agent
     * @return Agent object if present, empty otherwise
     */
    public Optional<Agent> getByID(Long id) {
        return agentRepository.findById(id);
    }
}
