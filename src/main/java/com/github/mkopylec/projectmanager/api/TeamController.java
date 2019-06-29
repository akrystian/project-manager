package com.github.mkopylec.projectmanager.api;

import com.github.mkopylec.projectmanager.application.TeamService;
import com.github.mkopylec.projectmanager.application.dto.NewTeam;
import org.springframework.web.bind.annotation.*;

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

