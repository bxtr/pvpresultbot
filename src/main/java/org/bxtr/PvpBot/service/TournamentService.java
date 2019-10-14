package org.bxtr.PvpBot.service;

import org.bxtr.PvpBot.model.Tournament;
import org.bxtr.PvpBot.repository.TournamentCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TournamentService {

    private final TournamentCrudRepository tournamentCrudRepository;

    public TournamentService(TournamentCrudRepository tournamentCrudRepository) {
        this.tournamentCrudRepository = tournamentCrudRepository;
    }

    public Tournament getTournament() {
        return tournamentCrudRepository.findById(1L).orElse(null);
    }
}
