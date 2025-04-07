package com.github.mkopylec.projectmanager.team


import com.github.mkopylec.projectmanager.team.core.IncomingDto
import com.github.mkopylec.projectmanager.team.inbound.http.ResponseBodies
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.CONFLICT
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

class TeamCreateAndLoadingSpecification extends TeamSpecification {

    def "Should create new team and browse it"() {
        given:
        def newTeam1 = new IncomingDto.NewTeam('Team_1')

        when:
        def response = team.createTeam(newTeam1)

        then:
        with(response) {
            status == CREATED
        }

        when:
        response = team.loadTeams()

        then:
        with(response) {
            status == OK
            body != null
            body.size() == 1
            with(body[0]) {
                name == 'Team_1'
                currentlyImplementedProjects == 0
                !busy
                members == []
            }
        }
    }

    @Unroll
    def "Should not create an unnamed new team"() {
        given:
        def newTeam = new IncomingDto.NewTeam(name)

        when:
        def response = team.createTeam(newTeam)

        then:
        with(response) {
            status == UNPROCESSABLE_ENTITY
            with(failure) {
                code == ResponseBodies.FailureCodeBody.EMPTY_TEAM_NAME
            }
        }

        where:
        name << [null, '', '  ']
    }

    def "Should not create a team that already exists"() {
        given:
        def newTeam = new IncomingDto.NewTeam('Team_1')
        team.createTeam(newTeam)

        when:
        def response = team.createTeam(newTeam)

        then:
        with(response) {
            status == CONFLICT
            with(failure) {
                code == ResponseBodies.FailureCodeBody.CONCURRENT_TEAM_MODIFICATION
            }
        }
    }

    def "Should browse teams if none exists"() {
        when:
        def response = team.loadTeams()

        then:
        response.status == OK
        response.body == []
    }
}
