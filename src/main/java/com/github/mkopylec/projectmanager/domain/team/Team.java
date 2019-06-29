package com.github.mkopylec.projectmanager.domain.team;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;

import java.util.Objects;

import static com.github.mkopylec.projectmanager.domain.exceptions.ErrorCode.EMPTY_TEAM_NAME;
import static com.github.mkopylec.projectmanager.domain.exceptions.PreCondition.when;

public class Team {
    @Id
    private final String name;

    public Team(String name) {
        validateName(name);
        this.name = name;
    }

    private void validateName(String name) {
        when(!StringUtils.isNotBlank(name))
                .thenInvalidEntity(EMPTY_TEAM_NAME, "Team name cannot be empty!");
    }

    String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
