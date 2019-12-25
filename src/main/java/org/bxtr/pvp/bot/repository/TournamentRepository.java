package org.bxtr.pvp.bot.repository;

import org.bxtr.pvp.bot.domain.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    Optional<Tournament> findFirstByTournamentName(String tournamentName);
}
