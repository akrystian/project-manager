package com.github.mkopylec.projectmanager.project

import com.github.mkopylec.projectmanager.project.inbound.http.RequestBodies
import com.github.mkopylec.projectmanager.team.core.IncomingDto
import com.github.mkopylec.projectmanager.team.utils.api.TeamHttpClient
import org.springframework.beans.factory.annotation.Autowired
import spock.util.concurrent.PollingConditions

import static com.github.mkopylec.projectmanager.project.inbound.http.RequestBodies.CompletionStatusBody.TO_DO
import static com.github.mkopylec.projectmanager.project.inbound.http.RequestBodies.FeatureRequirementBody.OPTIONAL
import static com.github.mkopylec.projectmanager.project.inbound.http.RequestBodies.NewProjectBody
import static com.github.mkopylec.projectmanager.project.inbound.http.RequestBodies.UpdatedProjectBody
import static com.github.mkopylec.projectmanager.project.utils.values.SampleValues.FEATURE_NAME
import static com.github.mkopylec.projectmanager.project.utils.values.SampleValues.OTHER_FEATURE_NAME
import static com.github.mkopylec.projectmanager.project.utils.values.SampleValues.OTHER_PROJECT_NAME
import static com.github.mkopylec.projectmanager.project.utils.values.SampleValues.PROJECT_NAME
import static com.github.mkopylec.projectmanager.project.utils.values.SampleValues.TEAM_NAME
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

class E2ESpecification extends ProjectSpecification {
    @Autowired
    protected TeamHttpClient team

    def conditions = new PollingConditions(timeout: 5)


    def "should update project with team assignment"() {
        given:
        def newTeam = new IncomingDto.NewTeam('Avengers')
        team.createTeam(newTeam)

        def newFeature = new RequestBodies.NewFeatureBody(FEATURE_NAME, OPTIONAL)
        def newProject = new NewProjectBody(PROJECT_NAME, [newFeature])
        projects.createProject(newProject)
        def projId = projects.loadProjectDrafts().body.projectDrafts()[0].id()
        def updatedFeature = new RequestBodies.UpdatedFeatureBody(OTHER_FEATURE_NAME, TO_DO, OPTIONAL)
        def updatedProject = new UpdatedProjectBody(OTHER_PROJECT_NAME, TEAM_NAME, [updatedFeature])

        when:
        def response = projects.updateProject(projId, updatedProject)

        then:
        with(response) {
            status == NO_CONTENT
        }

        and:
        conditions.eventually {
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

    def "should end project"() {
        given:
        def newTeam = new IncomingDto.NewTeam('Avengers')
        team.createTeam(newTeam)

        def newFeature = new RequestBodies.NewFeatureBody(FEATURE_NAME, OPTIONAL)
        def newProject = new NewProjectBody(PROJECT_NAME, [newFeature])
        projects.createProject(newProject)
        def projId = projects.loadProjectDrafts().body.projectDrafts()[0].id()
        def updatedFeature = new RequestBodies.UpdatedFeatureBody(OTHER_FEATURE_NAME, TO_DO, OPTIONAL)
        def updatedProject = new UpdatedProjectBody(OTHER_PROJECT_NAME, TEAM_NAME, [updatedFeature])
        def response = projects.updateProject(projId, updatedProject)
        projects.startProject(projId)


        when:
        projects.endProject(projId, new RequestBodies.ProjectEndingBody(RequestBodies.ProjectEndingConditionBody.ONLY_NECESSARY_FEATURES_DONE))

        then:
        with(response) {
            status == NO_CONTENT
        }

        and:
        conditions.eventually {
            with(team.loadTeams()) {
                status == OK
                body != null
                body.size() == 1
                with(body[0]) {
                    currentlyImplementedProjects == 0
                }
            }
        }
    }
}
