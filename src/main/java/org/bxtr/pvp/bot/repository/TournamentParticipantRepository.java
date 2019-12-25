package org.bxtr.pvp.bot.repository;

import org.bxtr.pvp.bot.domain.Tournament;
import org.bxtr.pvp.bot.domain.TournamentParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Long> {

    List<TournamentParticipant> findByTournament(Tournament tournament);
}
