package com.github.mkopylec.projectmanager.team.inbound.local;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mkopylec.projectmanager.common.inbound.local.EventHandler;
import com.github.mkopylec.projectmanager.common.inbound.local.JsonEventPayload;
import com.github.mkopylec.projectmanager.project.core.ProjectTeamAssigned;
import com.github.mkopylec.projectmanager.team.core.IncomingDto;
import com.github.mkopylec.projectmanager.team.core.TeamService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ProjectEventHandler extends EventHandler<ProjectTeamAssignedPayload> {
    private final TeamService service;

    protected ProjectEventHandler(TeamService teamService, ObjectMapper jsonMapper) {
        super(ProjectTeamAssigned.class.getSimpleName(), ProjectTeamAssignedPayload.class, jsonMapper);
        this.service = teamService;
    }

    @Override
    protected void handle(ProjectTeamAssignedPayload event) {
        IncomingDto.TeamName teamName = new IncomingDto.TeamName(event.getAssignedTeam());
        service.addCurrentlyImplementedProjectToTeam(teamName);
    }
}

class ProjectTeamAssignedPayload extends JsonEventPayload {
    private final String assignedTeam;

    @JsonCreator
    ProjectTeamAssignedPayload(
        @JsonProperty("eventId") UUID eventId,
        @JsonProperty("occurrenceDate") Instant occurrenceDate,
        @JsonProperty("assignedTeam") String assignedTeam
    ) {
        super(eventId, occurrenceDate);
        this.assignedTeam = assignedTeam;
    }

    String getAssignedTeam() {
        return assignedTeam;
    }
}
