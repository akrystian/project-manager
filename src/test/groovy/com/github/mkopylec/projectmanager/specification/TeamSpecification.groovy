package com.github.mkopylec.projectmanager.specification

import com.github.mkopylec.projectmanager.application.dto.NewTeam
import com.github.mkopylec.projectmanager.application.dto.TeamMember
import com.github.mkopylec.projectmanager.common.Specification
import com.github.mkopylec.projectmanager.project.utils.api.TeamHttpClient
import com.github.mkopylec.projectmanager.project.utils.event.ProjectPublishedEvents
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.*

class TeamSpecification extends Specification {

    @Autowired
    protected TeamHttpClient team
    @Autowired
    protected ProjectPublishedEvents publishedEvents

    @Override
    void cleanup() {
        publishedEvents.clear()
    }

    def "Should create new team and browse it"() {
        given:
        def newTeam1 = new NewTeam(name: 'Team_1')

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
        def newTeam = new NewTeam(name: name)

        when:
        def response = team.createTeam(newTeam)

        then:
        with(response) {
            status == UNPROCESSABLE_ENTITY
            with(failure) {
                code == 'EMPTY_TEAM_NAME'
            }
        }

        where:
        name << [null, '', '  ']
    }

    def "Should not create a team that already exists"() {
        given:
        def newTeam = new NewTeam(name: 'Team_1')
        team.createTeam(newTeam)

        when:
        def response = team.createTeam(newTeam)

        then:
        with(response) {
            status == UNPROCESSABLE_ENTITY
            with(failure) {
                code == 'TEAM_ALREADY_EXISTS'
            }
        }
    }

    @Unroll
    def "Should add a new member with #jobPosition job position to a team and browse him"() {
        given:
        def newTeam = new NewTeam(name: 'Team_1')
        team.createTeam(newTeam)
        def member = new TeamMember(firstName: 'Mariusz', lastName: 'Kopylec', jobPosition: jobPosition)

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
        jobPosition << ['DEVELOPER', 'SCRUM_MASTER', 'PRODUCT_OWNER']
    }

    @Unroll
    def "Should not add a new member without a first name to a team"() {
        given:
        def newTeam = new NewTeam(name: 'Team_1')
        team.createTeam(newTeam)
        def member = new TeamMember(firstName: firstName, lastName: 'Kopylec', jobPosition: 'DEVELOPER')

        when:
        def response = team.addMemberToTeam('Team_1', member)

        then:
        with(response) {
            status == UNPROCESSABLE_ENTITY
            with(failure) {
                code == 'EMPTY_MEMBER_FIRST_NAME'
            }
        }

        where:
        firstName << [null, '', '  ']
    }

    @Unroll
    def "Should not add a new member without a last name to a team"() {
        given:
        def newTeam = new NewTeam(name: 'Team_1')
        team.createTeam(newTeam)
        def member = new TeamMember(firstName: 'Mariusz', lastName: lastName, jobPosition: 'DEVELOPER')

        when:
        def response = team.addMemberToTeam('Team_1', member)

        then:
        with(response) {
            status == UNPROCESSABLE_ENTITY
            with(failure) {
                code == 'EMPTY_MEMBER_LAST_NAME'
            }
        }

        where:
        lastName << [null, '', '  ']
    }

    def "Should not add a new member with #jobPosition job position to a team"() {
        given:
        def newTeam = new NewTeam(name: 'Team_1')
        team.createTeam(newTeam)
        def member = new TeamMember(firstName: 'Mariusz', lastName: 'Kopylec', jobPosition: jobPosition)

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
        null                   | 'EMPTY_MEMBER_JOB_POSITION'
        ''                     | 'EMPTY_MEMBER_JOB_POSITION'
        '  '                   | 'EMPTY_MEMBER_JOB_POSITION'
        'INVALID_JOB_POSITION' | 'INVALID_MEMBER_JOB_POSITION'
    }

    def "Should not add a new member to a nonexistent team"() {
        given:
        def member = new TeamMember(firstName: 'Mariusz', lastName: 'Kopylec', jobPosition: 'DEVELOPER')

        when:
        def response = team.addMemberToTeam('Team_1', member)

        then:
        with(response) {
            status == NOT_FOUND
            with(failure) {
                code == 'NONEXISTENT_TEAM'
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
