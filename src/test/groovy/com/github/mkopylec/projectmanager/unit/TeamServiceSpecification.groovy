package com.github.mkopylec.projectmanager.unit

import com.github.mkopylec.projectmanager.application.TeamService
import com.github.mkopylec.projectmanager.application.dto.NewTeam
import com.github.mkopylec.projectmanager.domain.exceptions.EntityAlreadyExistsException
import com.github.mkopylec.projectmanager.domain.team.Team
import com.github.mkopylec.projectmanager.domain.team.TeamRepository
import org.springframework.data.domain.Example
import spock.lang.Specification

class TeamServiceSpecification extends Specification {

    TeamRepository teamRepository = Mock()

    TeamService underTest

    def 'setup'() {
        underTest = new TeamService(teamRepository)
    }

    def 'should create user'() {
        given:
        final TEAM_NAME = 'teamName'
        teamRepository.save(_ as Team) >> new Team(TEAM_NAME)
        teamRepository.exists(_) >> false

        when:
        underTest.createNewTeam(prepareNewTeam(TEAM_NAME))

        then:
        1 * teamRepository.save(_ as Team)
    }

    def 'should not crete duplicate user'() {
        given:
        final TEAM_NAME = 'teamName'
        teamRepository.exists(_ as Example) >>> [false, true]
        teamRepository.save(_ as Team) >> new Team(TEAM_NAME)

        when:
        underTest.createNewTeam(prepareNewTeam(TEAM_NAME))

        and:
        underTest.createNewTeam(prepareNewTeam(TEAM_NAME))

        then:
        thrown(EntityAlreadyExistsException)
        1 * teamRepository.save(_ as Team)
    }

    private static NewTeam prepareNewTeam(String teamName) {
        def team = new NewTeam()
        team.name = teamName
        team
    }
}
