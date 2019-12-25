package org.bxtr.pvp.bot;

import lombok.extern.log4j.Log4j2;
import org.bxtr.pvp.bot.domain.Tournament;
import org.bxtr.pvp.bot.repository.TournamentRepository;
import org.bxtr.pvp.bot.service.TournamentService;
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
    private TournamentRepository tournamentRepository;

    @Test
    public void getPlayersFromTournament() {
        TournamentService tournamentService = new TournamentService(tournamentRepository);
        Tournament tournament = tournamentService.getCurrentTournament();
        tournament.getPlayers().forEach(player -> log.debug(player));
    }
}
