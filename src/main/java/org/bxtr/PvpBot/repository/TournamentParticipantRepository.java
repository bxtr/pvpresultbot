package org.bxtr.PvpBot.repository;

import org.bxtr.PvpBot.model.TournamentParticipant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentParticipantRepository extends CrudRepository<TournamentParticipant, Long> {

}
