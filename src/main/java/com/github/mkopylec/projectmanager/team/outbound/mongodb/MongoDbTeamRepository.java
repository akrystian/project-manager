package com.github.mkopylec.projectmanager.team.outbound.mongodb;

import com.github.mkopylec.projectmanager.common.core.BusinessRuleViolation;
import com.github.mkopylec.projectmanager.team.core.TeamRepository;
import com.github.mkopylec.projectmanager.team.core.Team;
import com.github.mkopylec.projectmanager.team.core.TeamName;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@Repository
public class MongoDbTeamRepository extends TeamRepository {

    private final MongoOperations mongoDb;

    public MongoDbTeamRepository(MongoOperations mongoDb) {
        this.mongoDb = mongoDb;
    }

    @Override
    protected Optional<Team> find(TeamName teamName) {
        var document = mongoDb.findById(teamName.getValue(), TeamDocument.class);
        return ofNullable(document).map(TeamDocument::toTeam);
    }

    @Override
    protected void save(Team aggregate, Function<Exception, ? extends BusinessRuleViolation> onConcurrentModification) {
        var document = new TeamDocument(aggregate);
        try {
            mongoDb.save(document);
        } catch (DuplicateKeyException | OptimisticLockingFailureException e) {
            throw onConcurrentModification.apply(e);
        }
    }

    @Override
    protected List<Team> findAll() {
        var documents = mongoDb.findAll(TeamDocument.class);
        return documents.stream().map(TeamDocument::toTeam).toList();
    }
}
