package org.bxtr.PvpBot.repository;

import org.bxtr.PvpBot.model.FightResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FightResultRepositoryJPA extends JpaRepository<FightResult, Long> {

    @Query(value = "SELECT * FROM fight_result " +
            "JOIN player one ON one.id = fight_result.one " +
            "JOIN player two ON two.id = fight_result.two " +
            "WHERE (lower(one.player_name) = lower(:player_one_name) AND lower(two.player_name) = lower(:player_two_name)) " +
            "OR (lower(two.player_name) = lower(:player_one_name) AND lower(one.player_name) = lower(:player_two_name))", nativeQuery = true)
    List<FightResult> findFightResultWith(@Param("player_one_name") String playerNameOne,
                                          @Param("player_two_name") String playerNameTwo);

    @Query(value = "SELECT * FROM fight_result " +
            "JOIN player one ON one.id = fight_result.one " +
            "JOIN player two ON two.id = fight_result.two " +
            "WHERE lower(one.player_name) = lower(:player_name) or lower(two.player_name) = lower(:player_name)", nativeQuery = true)
    List<FightResult> findFightResultWith(@Param("player_name") String playerName);
}
