package com.github.mkopylec.projectmanager.team

import com.github.mkopylec.projectmanager.common.Specification
import com.github.mkopylec.projectmanager.team.core.IncomingDto
import com.github.mkopylec.projectmanager.team.inbound.http.ResponseBodies
import com.github.mkopylec.projectmanager.team.utils.api.TeamHttpClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.CONFLICT
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

class TeamSpecification extends Specification {

    @Autowired
    protected TeamHttpClient team


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

    @Unroll
    def "Should add a new member with #jobPosition job position to a team and browse him"() {
        given:
        def newTeam = new IncomingDto.NewTeam('Team_1')
        team.createTeam(newTeam)
        def member = new IncomingDto.TeamMember('Mariusz', 'Kopylec', jobPosition)

        when:
        def response = team.addMemberToTeam('Team_1', member)

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
                members != null
                members.size() == 1

                with(members[0]) {
                    firstName == 'Mariusz'
                    lastName == 'Kopylec'
                    jobPosition == jobPosition
                }
            }
        }

        where:
        jobPosition << ['SOFTWARE_DEVELOPER', 'SCRUM_MASTER', 'PRODUCT_OWNER']
    }

    @Unroll
    def "Should not add a new member without a first name to a team"() {
        given:
        def newTeam = new IncomingDto.NewTeam('Team_1')
        team.createTeam(newTeam)
        def member = new IncomingDto.TeamMember(firstName, 'Kopylec', 'DEVELOPER')

        when:
        def response = team.addMemberToTeam('Team_1', member)

        then:
        with(response) {
            status == UNPROCESSABLE_ENTITY
            with(failure) {
                code == ResponseBodies.FailureCodeBody.EMPTY_MEMBER_FIRST_NAME
            }
        }

        where:
        firstName << [null, '', '  ']
    }

    @Unroll
    def "Should not add a new member without a last name to a team"() {
        given:
        def newTeam = new IncomingDto.NewTeam('Team_1')
        team.createTeam(newTeam)
        def member = new IncomingDto.TeamMember('Mariusz', lastName, 'DEVELOPER')

        when:
        def response = team.addMemberToTeam('Team_1', member)

        then:
        with(response) {
            status == UNPROCESSABLE_ENTITY
            with(failure) {
                code == ResponseBodies.FailureCodeBody.EMPTY_MEMBER_LAST_NAME
            }
        }

        where:
        lastName << [null, '', '  ']
    }

    def "Should not add a new member with #jobPosition job position to a team"() {
        given:
        def newTeam = new IncomingDto.NewTeam('Team_1')
        team.createTeam(newTeam)
        def member = new IncomingDto.TeamMember('Mariusz', 'Kopylec', jobPosition)

        when:
        def response = team.addMemberToTeam('Team_1', member)

        then:
        with(response) {
            status == UNPROCESSABLE_ENTITY
            with(failure) {
                code == errorCode
            }
        }

        where:
        jobPosition            | errorCode
        null                   | ResponseBodies.FailureCodeBody.EMPTY_MEMBER_JOB_POSITION
        ''                     | ResponseBodies.FailureCodeBody.EMPTY_MEMBER_JOB_POSITION
        '  '                   | ResponseBodies.FailureCodeBody.EMPTY_MEMBER_JOB_POSITION
        'INVALID_JOB_POSITION' | ResponseBodies.FailureCodeBody.INVALID_MEMBER_JOB_POSITION
    }

    def "Should not add a new member to a nonexistent team"() {
        given:
        def member = new IncomingDto.TeamMember('Mariusz', 'Kopylec', 'DEVELOPER')

        when:
        def response = team.addMemberToTeam('Team_1', member)

        then:
        with(response) {
            status == NOT_FOUND
            with(failure) {
                code == ResponseBodies.FailureCodeBody.NONEXISTENT_TEAM
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
