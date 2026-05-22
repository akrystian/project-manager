package com.github.mkopylec.projectmanager.team.core;

import com.github.mkopylec.projectmanager.common.core.EventPublisher;
import com.github.mkopylec.projectmanager.team.core.IncomingDto.NewTeam;
import com.github.mkopylec.projectmanager.team.core.OutgoingDto.ExistingTeam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final EventPublisher eventPublisher;
    private final TeamRepository repository;


    public TeamService(EventPublisher eventPublisher, TeamRepository repository) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }

    public void createTeam(NewTeam newTeam) {
        try {
            repository.save(new Team(new TeamName(newTeam.name())), eventPublisher);
        } catch (TeamBusinessRuleViolation violation) {
            throw new NewTeamNotCreated(violation);
        }
    }

    public List<ExistingTeam> getTeams() {
        try {
            return repository.findAll().stream().map(ExistingTeam::new).toList();
        } catch (TeamBusinessRuleViolation violation) {
            throw new TeamsNotLoaded(violation);
        }
    }

    public void addCurrentlyImplementedProjectToTeam(IncomingDto.TeamName teamName) {
        try {
            var team = repository.require(new TeamName(teamName.name()));
            team.addCurrentlyImplementedProject();
            repository.save(team, eventPublisher);
        } catch (TeamBusinessRuleViolation violation) {
            throw new TeamsNotLoaded(violation);
        }
    }

    public void addMemberToTeam(IncomingDto.TeamName teamName, IncomingDto.TeamMember teamMember) {
        try {
            var team = repository.require(new TeamName(teamName.name()));
            team.addMember(new Member(
                new MemberFirstName(teamMember.firstName()),
                new MemberLastName(teamMember.lastName()),
                JobPosition.valueOf(teamMember.jobPosition())));
            repository.save(team, eventPublisher);
        } catch (TeamBusinessRuleViolation violation) {
            throw new TeamMemberNotAdded(violation);
        }
    }

    public static final class NewTeamNotCreated extends TeamUseCaseViolation {
        public NewTeamNotCreated(TeamBusinessRuleViolation violation) {
            super(violation);
        }
    }

    public static final class TeamsNotLoaded extends TeamUseCaseViolation {
        public TeamsNotLoaded(TeamBusinessRuleViolation violation) {
            super(violation);
        }
    }

    public static final class TeamMemberNotAdded extends TeamUseCaseViolation {
        public TeamMemberNotAdded(TeamBusinessRuleViolation violation) {
            super(violation);
        }
    }
}


