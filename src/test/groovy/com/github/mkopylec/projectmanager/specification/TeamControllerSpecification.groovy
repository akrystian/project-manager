package com.github.mkopylec.projectmanager.specification

import com.github.mkopylec.projectmanager.api.TeamController
import com.github.mkopylec.projectmanager.domain.team.Team
import com.github.mkopylec.projectmanager.domain.team.TeamRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [TeamController])
class TeamControllerSpecification extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    TeamRepository teamRepository

    def 'should create user'() {
        given:
        final TEAM_NAME = 'teamName'
        def json = '{"name": "' + TEAM_NAME + '"}'
        teamRepository.save(_ as Team) >> new Team(TEAM_NAME)

        when:
        def result = mockMvc.perform(post('/teams')
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json)
        )
        then:
        result.andExpect(status().is2xxSuccessful())
        1 * teamRepository.save(_ as Team)
    }

    def 'should not crete duplicate user'() {
        given:
        final TEAM_NAME = 'teamName'
        def json = '{"name": "' + TEAM_NAME + '"}'

        teamRepository.existsById(_ as String) >>> [false, true]
        teamRepository.save(_ as Team) >> new Team(TEAM_NAME)

        when:
        mockMvc.perform(post('/teams')
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json)
        )

        and:
        def result = mockMvc.perform(post('/teams')
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json)
        )

        then:
        result.andExpect(status().is(422))
        1 * teamRepository.save(_ as Team)
    }

    def 'should not crate duplicated user'() {

    }

    @TestConfiguration
    static class IntegrationTestMockingConfig {
        private DetachedMockFactory factory = new DetachedMockFactory()

        @Bean
        TeamRepository teamRepository() {
            factory.Mock(TeamRepository)
        }
    }

}

