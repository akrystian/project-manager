package com.github.mkopylec.projectmanager.application;

import com.github.mkopylec.projectmanager.application.dto.ExistingProject;
import com.github.mkopylec.projectmanager.application.dto.ExistingProjectDraft;
import com.github.mkopylec.projectmanager.application.dto.NewProject;
import com.github.mkopylec.projectmanager.application.dto.NewProjectDraft;
import com.github.mkopylec.projectmanager.application.dto.UpdatedProject;
import com.github.mkopylec.projectmanager.application.utils.DtoMapper;
import com.github.mkopylec.projectmanager.domain.project.Project;
import com.github.mkopylec.projectmanager.domain.project.ProjectFactory;
import com.github.mkopylec.projectmanager.domain.project.ProjectRepository;
import com.github.mkopylec.projectmanager.domain.services.ProjectTeamAssigner;
import com.github.mkopylec.projectmanager.domain.team.Team;
import com.github.mkopylec.projectmanager.domain.team.TeamRepository;
import com.github.mkopylec.projectmanager.domain.values.Feature;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.mkopylec.projectmanager.application.utils.DtoMapper.mapToExistingProject;
import static com.github.mkopylec.projectmanager.application.utils.DtoMapper.mapToExistingProjectDrafts;
import static com.github.mkopylec.projectmanager.application.utils.DtoMapper.mapToFeatures;
import static com.github.mkopylec.projectmanager.domain.exceptions.ErrorCode.NONEXISTENT_PROJECT;
import static com.github.mkopylec.projectmanager.domain.exceptions.PreCondition.when;

@Service
public class ProjectService {

    private ProjectFactory projectFactory;
    private ProjectRepository projectRepository;
    private TeamRepository teamRepository;
    private ProjectTeamAssigner projectTeamAssigner;

    public ProjectService(ProjectFactory projectFactory, ProjectRepository projectRepository, TeamRepository teamRepository, ProjectTeamAssigner projectTeamAssigner) {
        this.projectFactory = projectFactory;
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.projectTeamAssigner = projectTeamAssigner;
    }

    public void createProject(NewProjectDraft newProjectDraft) {
        Project project = projectFactory.createProjectDraft(newProjectDraft.getName());
        projectRepository.save(project);
    }

    public void createProject(NewProject newProject) {
        List<Feature> features = DtoMapper.mapNewToFeatures(newProject.getFeatures());
        Project project = projectFactory.createFullProject(newProject.getName(), features);
        projectRepository.save(project);
    }

    public List<ExistingProjectDraft> getProjects() {
        List<Project> projects = projectRepository.findAll();
        return mapToExistingProjectDrafts(projects);
    }

    public ExistingProject getProject(String projectIdentifier) {
        Project project = projectRepository.findByIdentifier(projectIdentifier);
        when(project == null)
                .thenMissingEntity(NONEXISTENT_PROJECT, "Error getting '" + projectIdentifier + "' project");
        return mapToExistingProject(project);
    }

    public void updateProject(String projectIdentifier, UpdatedProject updatedProject) {
        Project project = projectRepository.findByIdentifier(projectIdentifier);
        when(project == null)
                .thenMissingEntity(NONEXISTENT_PROJECT, "Error updating '" + projectIdentifier + "' project");
        List<Feature> features = mapToFeatures(updatedProject.getFeatures());
        project.rename(updatedProject.getName());
        project.updateFeatures(features);
        Team team = teamRepository.findByName(updatedProject.getTeam());
        projectTeamAssigner.assignTeamToProject(team, project);
        projectRepository.save(project);
        teamRepository.save(team);
    }
}
