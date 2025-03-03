package com.github.mkopylec.projectmanager.team.outbound.mongodb;


import com.github.mkopylec.projectmanager.common.core.AggregateStateVersion;
import com.github.mkopylec.projectmanager.team.core.BusyTeamThreshold;
import com.github.mkopylec.projectmanager.team.core.JobPosition;
import com.github.mkopylec.projectmanager.team.core.Member;
import com.github.mkopylec.projectmanager.team.core.MemberFirstName;
import com.github.mkopylec.projectmanager.team.core.MemberLastName;
import com.github.mkopylec.projectmanager.team.core.Team;
import com.github.mkopylec.projectmanager.team.core.TeamCurrentlyImplementedProjects;
import com.github.mkopylec.projectmanager.team.core.TeamName;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

import static com.github.mkopylec.projectmanager.common.support.ListUtils.mapToUnmodifiable;


@Document(collection = "teams")
public class TeamDocument {
    @MongoId
    private final String name;

    @Version
    private final Integer version;

    private final Integer busyThreshold;

    private final Integer currentlyImplementedProjects;

    private final List<MemberDocument> members;

    @PersistenceCreator
    private TeamDocument(String name, Integer version, Integer busyThreshold, Integer currentlyImplementedProjects, List<MemberDocument> members) {
        this.name = name;
        this.version = version;
        this.busyThreshold = busyThreshold;
        this.currentlyImplementedProjects = currentlyImplementedProjects;
        this.members = members;
    }

    public TeamDocument(Team team) {
        this(
            team.getName().getValue(),
            team.getStateVersion().getValue(),
            team.getBusyThreshold().getValue(),
            team.getCurrentlyImplementedProjects().getValue(),
            mapToUnmodifiable(team.getMembers(), MemberDocument::new)
        );
    }

    public Team toTeam() {
        return Team.fromPersistentState(
            TeamName.fromPersistentState(name),
            AggregateStateVersion.fromPersistentState(version),
            BusyTeamThreshold.fromPersistentState(busyThreshold),
            TeamCurrentlyImplementedProjects.fromPersistentState(currentlyImplementedProjects),
            mapToUnmodifiable(members, MemberDocument::toMember)
        );
    }


    private static class MemberDocument {
        private final String firstName;
        private final String lastName;

        private final String jobPosition;

        @PersistenceCreator
        private MemberDocument(String firstName, String lastName, String jobPosition) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.jobPosition = jobPosition;
        }

        private MemberDocument(Member member) {
            this(member.getFirstName().getValue(), member.getLastName().getValue(), member.getJobPosition().getValue());
        }

        private Member toMember() {
            return Member.fromPersistentState(
                MemberFirstName.fromPersistentState(firstName),
                MemberLastName.fromPersistentState(lastName),
                JobPosition.fromPersistentState(jobPosition)
            );
        }
    }
}
