package com.github.mkopylec.projectmanager.team.inbound.local;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mkopylec.projectmanager.common.inbound.local.JsonEventPayload;

import java.time.Instant;
import java.util.UUID;

class ProjectEndedPayload extends JsonEventPayload {
    private final UUID projectId;

    @JsonCreator
    ProjectEndedPayload(
        @JsonProperty("eventId") UUID eventId,
        @JsonProperty("occurrenceDate") Instant occurrenceDate,
        @JsonProperty("projectId") UUID projectId
    ) {
        super(eventId, occurrenceDate);
        this.projectId = projectId;
    }

    UUID getProjectId() {
        return projectId;
    }
}

