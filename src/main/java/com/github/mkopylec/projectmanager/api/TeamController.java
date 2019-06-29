package com.github.mkopylec.projectmanager.api;

import com.github.mkopylec.projectmanager.application.dto.NewTeam;
import com.github.mkopylec.projectmanager.domain.exceptions.PreCondition;
import com.github.mkopylec.projectmanager.domain.team.Team;
import com.github.mkopylec.projectmanager.domain.team.TeamRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import static com.github.mkopylec.projectmanager.domain.exceptions.ErrorCode.TEAM_ALREADY_EXISTS;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;


    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @ResponseStatus(CREATED)
    @PostMapping(consumes = "application/json", produces = "application/json")
    public void createTeam(@RequestBody NewTeam newTeam) {
        teamService.createNewTeam(newTeam);
    }


}

@Service
class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    void createNewTeam(NewTeam newTeam) {
        final Team team = new Team(newTeam.getName());
        final boolean exists = teamRepository.exists(Example.of(team));
        PreCondition.when(exists).thenEntityAlreadyExists(TEAM_ALREADY_EXISTS, "");
        teamRepository.save(team);
    }
}
