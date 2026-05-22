package com.github.mkopylec.projectmanager.team

import com.github.mkopylec.projectmanager.team.core.IncomingDto
import com.github.mkopylec.projectmanager.team.inbound.local.ProjectTeamAssignedPayload

import static java.time.Instant.now
import static java.util.UUID.randomUUID
import static org.springframework.http.HttpStatus.OK

class TeamConsumeProjectAssignedSpecification extends TeamSpecification {

    def "Should consume project assigned"() {
        given:
        def newTeam = new IncomingDto.NewTeam('Team_1')
        team.createTeam(newTeam)

        and:
        with(team.loadTeams()) {
            status == OK
            body != null
            body.size() == 1
            with(body[0]) {
                currentlyImplementedProjects == 0
            }
        }

        and:
        def projectAssigned = new ProjectTeamAssignedPayload(randomUUID(), now(), 'Team_1')

        when:
        projectEventHandler.handle(projectAssigned)

        then:
        with(team.loadTeams()) {
            status == OK
            body != null
            body.size() == 1
            with(body[0]) {
                currentlyImplementedProjects == 1
            }
        }

    }
}
