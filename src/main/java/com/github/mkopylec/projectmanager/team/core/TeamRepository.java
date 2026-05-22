package com.github.mkopylec.projectmanager.team.core;

import com.github.mkopylec.projectmanager.common.core.AggregateRepository;
import com.github.mkopylec.projectmanager.common.core.EventPublisher;

import java.util.List;

import static com.github.mkopylec.projectmanager.common.core.BusinessRuleViolationProperties.properties;

public abstract class TeamRepository extends AggregateRepository<Team, TeamName> {

    protected void save(Team aggregate, EventPublisher publisher) {
        super.save(aggregate, publisher, (e) -> {
            throw new ConcurrentTeamModification(aggregate.getName(), e);
        });
    }

    protected Team require(TeamName teamName) {
        return find(teamName).orElseThrow(() -> new NoTeamExists(teamName));
    }

    protected abstract List<Team> findAll();

    static final class ConcurrentTeamModification extends TeamBusinessRuleViolation {
        private ConcurrentTeamModification(TeamName id, Exception cause) {
            super(properties("teamName", id), cause);
        }
    }

    static final class NoTeamExists extends TeamBusinessRuleViolation {
        private NoTeamExists(TeamName teamName) {
            super(properties("teamName", teamName));
        }
    }

    static final class TeamAlreadyExists extends TeamBusinessRuleViolation {
        private TeamAlreadyExists(TeamName teamName) {
            super(properties("teamName", teamName));
        }
    }
}


