package com.github.mkopylec.projectmanager.team.core;

import com.github.mkopylec.projectmanager.common.core.UseCaseViolation;

import com.github.mkopylec.projectmanager.team.core.TeamService.NewTeamNotCreated;
import com.github.mkopylec.projectmanager.team.core.TeamService.TeamsNotLoaded;

import static com.github.mkopylec.projectmanager.team.core.OutgoingDto.ViolationCode.violationCode;

public abstract sealed class TeamUseCaseViolation extends UseCaseViolation permits NewTeamNotCreated, TeamService.TeamMemberNotAdded, TeamsNotLoaded {

    TeamUseCaseViolation(TeamBusinessRuleViolation violation) {
        super(violationCode(violation), violation);
    }

    public OutgoingDto.ViolationCode getCode() {
        return super.getCode(OutgoingDto.ViolationCode.class);
    }
}
