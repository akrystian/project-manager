package com.github.mkopylec.projectmanager.application;

import com.github.mkopylec.projectmanager.application.dto.NewTeam;
import com.github.mkopylec.projectmanager.domain.exceptions.PreCondition;
import com.github.mkopylec.projectmanager.domain.team.Team;
import com.github.mkopylec.projectmanager.domain.team.TeamRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import static com.github.mkopylec.projectmanager.domain.exceptions.ErrorCode.TEAM_ALREADY_EXISTS;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public void createNewTeam(NewTeam newTeam) {
        final Team team = new Team(newTeam.getName());
        final boolean exists = teamRepository.exists(Example.of(team));
        PreCondition.when(exists).thenEntityAlreadyExists(TEAM_ALREADY_EXISTS, "");
        teamRepository.save(team);
    }
}
