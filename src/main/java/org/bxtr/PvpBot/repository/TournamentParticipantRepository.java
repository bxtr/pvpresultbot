package org.bxtr.PvpBot.repository;

import org.bxtr.PvpBot.model.Tournament;
import org.bxtr.PvpBot.model.TournamentParticipant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentParticipantRepository extends CrudRepository<TournamentParticipant, Long> {

    List<TournamentParticipant> findByTournament(Tournament tournament);
}
