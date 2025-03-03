package com.github.mkopylec.projectmanager.team.core;

import com.github.mkopylec.projectmanager.common.core.UseCaseViolation.CommonViolationCode;
import com.github.mkopylec.projectmanager.team.core.BusyTeamThreshold.InvalidBusyTeamThreshold;
import com.github.mkopylec.projectmanager.team.core.JobPosition.InvalidJobPosition;
import com.github.mkopylec.projectmanager.team.core.MemberFirstName.InvalidEmployeeFirstName;
import com.github.mkopylec.projectmanager.team.core.MemberLastName.InvalidEmployeeLastName;
import com.github.mkopylec.projectmanager.team.core.TeamCurrentlyImplementedProjects.InvalidTeamCurrentlyImplementedProjects;
import com.github.mkopylec.projectmanager.team.core.TeamName.InvalidTeamName;
import com.github.mkopylec.projectmanager.team.core.TeamRepository.ConcurrentTeamModification;

import java.util.List;

public class OutgoingDto {
    public enum ViolationCode implements CommonViolationCode {
        CONCURRENT_TEAM_MODIFICATION,
        EMPTY_MEMBER_FIRST_NAME,
        EMPTY_MEMBER_LAST_NAME,

        INVALID_MEMBER_JOB_POSITION,
        EMPTY_MEMBER_JOB_POSITION,
        NONEXISTENT_TEAM,
        TEAM_ALREADY_EXISTS,
        EMPTY_TEAM_NAME;

        static ViolationCode violationCode(TeamBusinessRuleViolation violation) {
            return switch (violation) {
                case ConcurrentTeamModification concurrentTeamModification -> CONCURRENT_TEAM_MODIFICATION;
                case InvalidTeamName invalidTeamName -> EMPTY_TEAM_NAME;
                case InvalidBusyTeamThreshold invalidBusyTeamThreshold -> EMPTY_TEAM_NAME;
                case InvalidJobPosition invalidJobPosition -> INVALID_MEMBER_JOB_POSITION;
                case InvalidEmployeeFirstName invalidEmployeeFirstName -> EMPTY_MEMBER_FIRST_NAME;
                case InvalidEmployeeLastName invalidEmployeeLastName -> EMPTY_MEMBER_LAST_NAME;
                case InvalidTeamCurrentlyImplementedProjects invalidTeamCurrentlyImplementedProjects -> EMPTY_TEAM_NAME;
                case TeamRepository.NoTeamExists noTeamExists -> NONEXISTENT_TEAM;
                case JobPosition.EmptyJobPosition emptyJobPosition -> EMPTY_MEMBER_JOB_POSITION;
                case TeamRepository.TeamAlreadyExists teamAlreadyExists -> TEAM_ALREADY_EXISTS;
            };
        }
    }

    public record ExistingTeam(
        String name,
        Integer currentlyImplementedProjects,
        List<TeamMember> members
    ) {
        public ExistingTeam(Team team) {
            this(team.getName().getValue(),
                team.getCurrentlyImplementedProjects().getValue(),
                team.getMembers().stream().map(TeamMember::new).toList());
        }
    }


    public record TeamMember(
        String firstName,
        String lastName,
        String jobPosition
    ) {
        public TeamMember(Member member) {
            this(member.getFirstName().getValue(), member.getLastName().getValue(), member.getJobPosition().getValue());
        }
    }
}
