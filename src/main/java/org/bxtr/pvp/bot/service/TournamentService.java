package org.bxtr.pvp.bot.service;

import lombok.RequiredArgsConstructor;
import org.bxtr.pvp.bot.repository.TournamentRepository;
import org.bxtr.pvp.bot.domain.Tournament;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    @Value("${pvp_bot.challonge.curent_tournament}")
    private String currentTournamentName;

    public Tournament getCurrentTournament() {
        return tournamentRepository.findFirstByTournamentName(currentTournamentName)
                .orElse(null);
    }
}
