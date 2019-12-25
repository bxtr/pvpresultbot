package org.bxtr.pvp.bot.service;

import lombok.AllArgsConstructor;
import org.bxtr.pvp.bot.repository.TournamentParticipantRepository;
import org.bxtr.pvp.bot.domain.TournamentParticipant;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TournamentParticipantService {

    private final TournamentParticipantRepository tournamentParticipantRepository;

    public List<TournamentParticipant> getAll() {
        return tournamentParticipantRepository.findAll();
    }
}
