package com.github.mkopylec.projectmanager.team.inbound.http;

import com.github.mkopylec.projectmanager.common.inbound.http.FailureResponseHandler;
import com.github.mkopylec.projectmanager.team.core.TeamUseCaseViolation;
import com.github.mkopylec.projectmanager.team.inbound.http.ResponseBodies.FailureBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.github.mkopylec.projectmanager.team.inbound.http.ResponseBodies.FailureCodeBody.UNEXPECTED_ERROR;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestControllerAdvice(basePackageClasses = TeamController.class)
class TeamFailureHandler extends FailureResponseHandler {

    @ExceptionHandler(TeamUseCaseViolation.class)
    ResponseEntity<FailureBody> handle(TeamUseCaseViolation violation, HttpServletRequest request) {
        HttpStatus status = switch (violation.getCode()) {
            case NONEXISTENT_TEAM -> NOT_FOUND;
            case CONCURRENT_TEAM_MODIFICATION -> CONFLICT;
            case EMPTY_MEMBER_FIRST_NAME, TEAM_ALREADY_EXISTS, EMPTY_MEMBER_LAST_NAME, INVALID_MEMBER_JOB_POSITION, EMPTY_MEMBER_JOB_POSITION, EMPTY_TEAM_NAME ->
                UNPROCESSABLE_ENTITY;
        };
        return handle(new FailureBody(violation), violation, request, status);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<FailureBody> handle(Exception exception, HttpServletRequest request) {
        return handle(new FailureBody(UNEXPECTED_ERROR), exception, request, INTERNAL_SERVER_ERROR);
    }
}
