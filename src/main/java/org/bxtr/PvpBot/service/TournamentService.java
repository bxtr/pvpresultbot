package org.bxtr.PvpBot.service;

import lombok.RequiredArgsConstructor;
import org.bxtr.PvpBot.model.Tournament;
import org.bxtr.PvpBot.repository.TournamentCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TournamentService {

    @Value("${pvpBot.challonge.curentTournament}")
    private String currentTournamentName;

    private final TournamentCrudRepository tournamentCrudRepository;

    public Tournament getCurrentTournament() {
        return tournamentCrudRepository.findFirstByTournamentName(currentTournamentName);
    }
}
