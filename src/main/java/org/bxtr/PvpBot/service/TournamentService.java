package org.bxtr.PvpBot.service;

import lombok.RequiredArgsConstructor;
import org.bxtr.PvpBot.model.Tournament;
import org.bxtr.PvpBot.repository.TournamentCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TournamentService {

    private final TournamentCrudRepository tournamentCrudRepository;

    public Tournament getTournament() {
        return tournamentCrudRepository.findById(1L).orElse(null);
    }
}
