package com.hoxsik.project.real_estate_agency.controllers;

import com.hoxsik.project.real_estate_agency.dto.Mapper;
import com.hoxsik.project.real_estate_agency.dto.request.UserRequest;
import com.hoxsik.project.real_estate_agency.dto.response.AgentResponse;
import com.hoxsik.project.real_estate_agency.dto.response.Response;
import com.hoxsik.project.real_estate_agency.jpa.entities.Agent;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Privilege;
import com.hoxsik.project.real_estate_agency.security.RequiredPrivilege;
import com.hoxsik.project.real_estate_agency.services.AgentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AgentController {
    private final AgentService agentService;

    @RequiredPrivilege(Privilege.ADD_AGENT)
    @PostMapping("/admin/add-agent")
    public ResponseEntity<Response> createAgentAccount(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UserRequest userRequest) {
        Response response = agentService.createAgentAccount(userRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/auth/agents")
    public ResponseEntity<List<AgentResponse>> getAllAgents() {
        Optional<List<Agent>> agents = agentService.getAllAgents();

        return agents
                .map(agentsList -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(agentsList.stream().map(Mapper.INSTANCE::convertAgent).toList()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @GetMapping("/auth/agent")
    public ResponseEntity<AgentResponse> checkAgent(@NotNull @RequestParam("id") Long id) {
        Optional<Agent> optionalAgent = agentService.getByID(id);

        return optionalAgent.map(agent -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Mapper.INSTANCE.convertAgent(agent)))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }
}
