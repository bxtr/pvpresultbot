package org.bxtr.PvpBot;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.model.Tournament;
import org.bxtr.PvpBot.repository.TournamentCrudRepository;
import org.bxtr.PvpBot.service.TournamentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class TournamentTests {

    @Autowired
    private TournamentCrudRepository tournamentCrudRepository;

    @Test
    public void getPlayersFromTournament() {
        TournamentService tournamentService = new TournamentService(tournamentCrudRepository);
        Tournament tournament = tournamentService.getTournament();
        tournament.getPlayers().forEach(player -> log.debug(player));
    }
}
