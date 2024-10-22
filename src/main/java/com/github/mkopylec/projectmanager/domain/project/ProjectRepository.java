package com.github.mkopylec.projectmanager.domain.project;

import java.util.List;

public interface ProjectRepository {

    Project findByIdentifier(String identifier);

    List<Project> findAll();

    void save(Project project);
}
