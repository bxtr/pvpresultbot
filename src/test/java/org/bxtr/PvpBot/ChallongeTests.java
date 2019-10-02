package org.bxtr.PvpBot;

import at.stefangeyer.challonge.Challonge;
import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Credentials;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.Tournament;
import at.stefangeyer.challonge.model.query.MatchQuery;
import at.stefangeyer.challonge.rest.retrofit.RetrofitRestClient;
import at.stefangeyer.challonge.serializer.gson.GsonSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChallongeTests {

    @Test
    public void getTournaments() {
        Credentials credentials = new Credentials("bxtr2121", "PAsygdCYddvMboCGGBTDSE8DWjXpNFBgmJvA28yk");
        Challonge challonge = new Challonge(credentials, new GsonSerializer(), new RetrofitRestClient());
        Tournament tournament = null;
        try {
            tournament = challonge.getTournaments().stream()
                    .filter(item -> item.getName().equals("Бот-тест"))
                    .findFirst().orElse(null);

            List<Match> matches = challonge.getMatches(tournament);
            Participant bxtr2121 = challonge.getParticipants(tournament)
                    .stream().filter(participant -> participant.getName().equals("@bxtr21")).findFirst()
                    .orElse(null);

            List<Match> filteredMatches = matches.stream().filter(match -> match.getPlayer1Id().equals(bxtr2121.getId())
                    || match.getPlayer2Id().equals(bxtr2121.getId()))
                    .collect(Collectors.toList());

            filteredMatches.forEach(match -> {
                try {
                    challonge.updateMatch(match, MatchQuery.builder().scoresCsv("1-0,0-1,1-0").winnerId(bxtr2121.getId()).build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(String.format("#%d %d %s %d %d", match.getPlayer1Id(), match.getPlayer2Id(), match.getScoresCsv(), match.getPlayer1Votes(), match.getPlayer2Votes()));
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}
