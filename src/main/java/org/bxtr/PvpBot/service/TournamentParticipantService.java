package org.bxtr.PvpBot.service;

import org.bxtr.PvpBot.model.TournamentParticipant;
import org.bxtr.PvpBot.repository.TournamentParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TournamentParticipantService {

    private final TournamentParticipantRepository tournamentParticipantRepository;

    public TournamentParticipantService(TournamentParticipantRepository tournamentParticipantRepository) {
        this.tournamentParticipantRepository = tournamentParticipantRepository;
    }


    public List<TournamentParticipant> getAll() {
        final List<TournamentParticipant> tournamentParticipants = new ArrayList<>();
        tournamentParticipantRepository.findAll().forEach(item -> tournamentParticipants.add(item));
        return tournamentParticipants;
    }
}
