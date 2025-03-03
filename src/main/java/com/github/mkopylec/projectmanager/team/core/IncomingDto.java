package com.github.mkopylec.projectmanager.team.core;

public class IncomingDto {

    public record NewTeam(String name) {
    }

    public record TeamMember(String firstName, String lastName, String jobPosition) {
    }

    public record TeamName(String name) {
    }
}
