package org.bxtr.pvp.bot.repository;

import org.bxtr.pvp.bot.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepositoryJPA extends JpaRepository<Team, Long> {
    Optional<Team> getByPlayerName(String playerName);
}
