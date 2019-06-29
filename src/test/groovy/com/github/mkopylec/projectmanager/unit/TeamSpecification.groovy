package com.github.mkopylec.projectmanager.unit

import com.github.mkopylec.projectmanager.domain.exceptions.ErrorCode
import com.github.mkopylec.projectmanager.domain.exceptions.InvalidEntityException
import com.github.mkopylec.projectmanager.domain.team.Team
import spock.lang.Specification

class TeamSpecification extends Specification {

    def 'should crate new team'() {
        given:
        final name = 'TeamName'

        when:
        def result = new Team(name)

        then:
        result != null
        result.getName() == name
    }

    def 'should validate missing team name'() {
        given:
        final name = ''

        when:
        new Team(name)

        then:
        InvalidEntityException ex = thrown()
        ex.code == ErrorCode.EMPTY_TEAM_NAME
    }
}
