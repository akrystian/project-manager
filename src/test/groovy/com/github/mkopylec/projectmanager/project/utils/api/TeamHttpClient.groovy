package com.github.mkopylec.projectmanager.project.utils.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mkopylec.projectmanager.application.dto.ExistingTeam
import com.github.mkopylec.projectmanager.application.dto.NewTeam
import com.github.mkopylec.projectmanager.application.dto.TeamMember
import com.github.mkopylec.projectmanager.common.utils.api.HttpClient
import com.github.mkopylec.projectmanager.common.utils.api.HttpRequest
import com.github.mkopylec.projectmanager.common.utils.api.HttpResponse
import com.github.mkopylec.projectmanager.common.utils.api.HttpResponseBodyReader
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.stereotype.Component

import static com.github.mkopylec.projectmanager.project.inbound.http.ResponseBodies.FailureBody
import static org.springframework.http.HttpMethod.GET
import static org.springframework.http.HttpMethod.POST

@Component
class TeamHttpClient extends HttpClient {

    private static final String PROJECTS_CONTEXT_PATH = '/teams'

    private TeamHttpClient(TestRestTemplate httpClient, HttpResponseBodyReader bodyReader, ObjectMapper mapper) {
        super(httpClient, bodyReader, mapper)
    }

    HttpResponse<Void, FailureBody> createTeam(NewTeam body) {
        def request = new HttpRequest()
            .setMethod(POST)
            .setUrl("$PROJECTS_CONTEXT_PATH")
            .setBody(body)
        sendRequest(request, Void, FailureBody)
    }

    HttpResponse<List<ExistingTeam>, FailureBody> loadTeams() {
        def request = new HttpRequest()
            .setMethod(GET)
            .setUrl("$PROJECTS_CONTEXT_PATH")
        sendRequest(request, List<ExistingTeam>, FailureBody)
    }

    HttpResponse<Void, FailureBody> addMemberToTeam(String teamName, TeamMember member) {
        def request = new HttpRequest()
            .setMethod(POST)
            .setUrl("$PROJECTS_CONTEXT_PATH/$teamName/members")
            .setBody(member)
        sendRequest(request, Void, FailureBody)
    }
}
