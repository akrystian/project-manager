package com.github.mkopylec.projectmanager.team.inbound.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mkopylec.projectmanager.common.inbound.http.FailureResponseBody;
import com.github.mkopylec.projectmanager.common.inbound.http.FailureResponseBody.FailureCodeResponseBody;
import com.github.mkopylec.projectmanager.team.core.OutgoingDto.ViolationCode;
import com.github.mkopylec.projectmanager.team.core.TeamUseCaseViolation;

import java.util.Map;

import static com.github.mkopylec.projectmanager.team.inbound.http.ResponseBodies.FailureCodeBody.failureCodeBody;


public class ResponseBodies {


    public static class FailureBody extends FailureResponseBody<FailureCodeBody> {

        @JsonCreator
        private FailureBody(@JsonProperty("code") FailureCodeBody code, @JsonProperty("properties") Map<String, Object> properties) {
            super(code, properties);
        }

        FailureBody(FailureCodeBody code) {
            super(code);
        }

        FailureBody(TeamUseCaseViolation violation) {
            super(failureCodeBody(violation.getCode()), violation.getProperties());
        }
    }

    public enum FailureCodeBody implements FailureCodeResponseBody {

        CONCURRENT_TEAM_MODIFICATION,
        EMPTY_TEAM_NAME,
        TEAM_ALREADY_EXISTS,
        EMPTY_MEMBER_JOB_POSITION,
        INVALID_MEMBER_JOB_POSITION,
        NONEXISTENT_TEAM,
        EMPTY_MEMBER_FIRST_NAME,
        EMPTY_MEMBER_LAST_NAME,
        UNEXPECTED_ERROR;

        static FailureCodeBody failureCodeBody(ViolationCode code) {
            return switch (code) {
                case CONCURRENT_TEAM_MODIFICATION -> CONCURRENT_TEAM_MODIFICATION;
                case EMPTY_MEMBER_LAST_NAME -> EMPTY_MEMBER_LAST_NAME;
                case INVALID_MEMBER_JOB_POSITION -> INVALID_MEMBER_JOB_POSITION;
                case EMPTY_MEMBER_JOB_POSITION -> EMPTY_MEMBER_JOB_POSITION;
                case NONEXISTENT_TEAM -> NONEXISTENT_TEAM;
                case TEAM_ALREADY_EXISTS -> TEAM_ALREADY_EXISTS;
                case EMPTY_TEAM_NAME -> EMPTY_TEAM_NAME;
                case EMPTY_MEMBER_FIRST_NAME -> EMPTY_MEMBER_FIRST_NAME;
            };
        }
    }
}
