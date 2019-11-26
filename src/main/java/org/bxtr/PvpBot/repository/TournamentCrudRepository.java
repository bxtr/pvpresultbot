package org.bxtr.PvpBot.repository;

import org.bxtr.PvpBot.model.Tournament;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentCrudRepository extends CrudRepository<Tournament, Long> {

    Tournament findFirstByTournamentName(String tournamentName);
}
