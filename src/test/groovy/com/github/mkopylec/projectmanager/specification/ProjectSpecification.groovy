package com.github.mkopylec.projectmanager.specification

import com.github.mkopylec.projectmanager.BasicSpecification
import com.github.mkopylec.projectmanager.application.dto.ExistingProjectDraft
import com.github.mkopylec.projectmanager.application.dto.NewFeature
import com.github.mkopylec.projectmanager.application.dto.NewProject
import com.github.mkopylec.projectmanager.application.dto.NewProjectDraft
import org.springframework.core.ParameterizedTypeReference
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

class ProjectSpecification extends BasicSpecification {

    def "Should create new project draft and browse it"() {
        given:
        def projectDraft = new NewProjectDraft(name: 'Project 1')

        when:
        def response = post('/projects/drafts', projectDraft)

        then:
        response.statusCode == CREATED

        when:
        response = get('/projects', new ParameterizedTypeReference<List<ExistingProjectDraft>>() {})

        then:
        response.statusCode == OK
        response.body != null
        response.body.size() == 1
        with(response.body[0]) {
            identifier != null
            name == 'Project 1'
        }
    }

    @Unroll
    def "Should not create an unnamed new project draft"() {
        given:
        def projectDraft = new NewProjectDraft(name: name)

        when:
        def response = post('/projects/drafts', projectDraft)

        then:
        response.statusCode == UNPROCESSABLE_ENTITY
        response.body.code == 'EMPTY_PROJECT_NAME'

        where:
        name << [null, '', '  ']
    }

    @Unroll
    def "Should create new full project with a #requirement feature and browse it"() {
        given:
        def feature = new NewFeature(name: 'Feature 1', requirement: requirement)
        def project = new NewProject(name: 'Project 1', features: [feature])

        when:
        def response = post('/projects', project)

        then:
        response.statusCode == CREATED

        when:
        response = get('/projects', new ParameterizedTypeReference<List<ExistingProjectDraft>>() {})

        then:
        response.statusCode == OK
        response.body != null
        response.body.size() == 1
        with(response.body[0]) {
            identifier != null
            name == 'Project 1'
        }

        where:
        requirement << ['OPTIONAL', 'RECOMMENDED', 'NECESSARY']
    }

    @Unroll
    def "Should not create an unnamed new full project"() {
        given:
        def project = new NewProject(name: name, features: [])

        when:
        def response = post('/projects', project)

        then:
        response.statusCode == UNPROCESSABLE_ENTITY
        response.body.code == 'EMPTY_PROJECT_NAME'

        where:
        name << [null, '', '  ']
    }

    @Unroll
    def "Should not create a new full project with unnamed feature"() {
        given:
        def feature = new NewFeature(name: name, requirement: 'NECESSARY')
        def project = new NewProject(name: 'Project 1', features: [feature])

        when:
        def response = post('/projects', project)

        then:
        response.statusCode == UNPROCESSABLE_ENTITY
        response.body.code == 'EMPTY_FEATURE_NAME'

        where:
        name << [null, '', '  ']
    }

    @Unroll
    def "Should not create a new full project with feature with #requirement requirement"() {
        given:
        def feature = new NewFeature(name: 'Feature 1', requirement: requirement)
        def project = new NewProject(name: 'Project 1', features: [feature])

        when:
        def response = post('/projects', project)

        then:
        response.statusCode == UNPROCESSABLE_ENTITY
        response.body.code == errorCode

        where:
        requirement           | errorCode
        null                  | 'EMPTY_FEATURE_REQUIREMENT'
        ''                    | 'EMPTY_FEATURE_REQUIREMENT'
        '  '                  | 'EMPTY_FEATURE_REQUIREMENT'
        'INVALID_REQUIREMENT' | 'INVALID_FEATURE_REQUIREMENT'
    }

    def "Should browse projects if none exists"() {
        when:
        def response = get('/projects', List)

        then:
        response.statusCode == OK
        response.body == []
    }
}
