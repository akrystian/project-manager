package com.github.mkopylec.projectmanager.team.inbound.http;

import com.github.mkopylec.projectmanager.project.core.IncomingDto.NewTeam;
import com.github.mkopylec.projectmanager.team.core.IncomingDto;
import com.github.mkopylec.projectmanager.team.core.OutgoingDto;
import com.github.mkopylec.projectmanager.team.core.TeamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/teams", produces = APPLICATION_JSON_VALUE)
public class TeamController {

        private final TeamService service;

        public TeamController(TeamService service) {
            this.service = service;
        }

    @ResponseStatus(CREATED)
    @PostMapping
    public void createTeam(@RequestBody NewTeam newTeam) {
        service.createTeam(newTeam);
    }

    @ResponseStatus(OK)
    @GetMapping
    public List<OutgoingDto.ExistingTeam> getTeams() {
        return service.getTeams();
    }

    @ResponseStatus(CREATED)
    @PostMapping("/{teamName}/members")
    public void addMemberToTeam(@PathVariable String teamName, @RequestBody IncomingDto.TeamMember teamMember) {
        service.addMemberToTeam(new IncomingDto.TeamName(teamName), teamMember);

    }
}
