package org.bxtr.PvpBot.repository;

import org.bxtr.PvpBot.model.Team;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepositoryJPA extends JpaRepository<Team, Long> {

    @Query(value = "SELECT team.* FROM team " +
            "JOIN player ON player.id = team.player_id " +
            "WHERE player.NAME = :player_name", nativeQuery = true)
    Team getTeam(@Value("player_name") String playerName);
}
