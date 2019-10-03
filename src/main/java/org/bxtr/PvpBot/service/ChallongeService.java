package org.bxtr.PvpBot.service;

import at.stefangeyer.challonge.Challonge;
import at.stefangeyer.challonge.model.Credentials;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Tournament;
import at.stefangeyer.challonge.model.query.MatchQuery;
import at.stefangeyer.challonge.rest.retrofit.RetrofitRestClient;
import at.stefangeyer.challonge.serializer.gson.GsonSerializer;
import org.bxtr.PvpBot.model.FightResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Scope("singleton")
public class ChallongeService {

    @Autowired
    FightResultService fightResultService;

    @Value("${pvpbot.challonge.username}")
    private String userName;

    @Value("${pvpbot.challonge.token}")
    private String token;

    public void update() {
        Credentials credentials = new Credentials(userName, token);
        Challonge challonge = new Challonge(credentials, new GsonSerializer(), new RetrofitRestClient());
        List<FightResult> fightResults = fightResultService.findAll();
        try {
            Tournament tournament = challonge.getTournaments().stream()
                    .filter(item -> item.getName().equals("Sinister cup"))
                    .findFirst().orElse(null);

            List<Match> matches = challonge.getMatches(tournament);
            Map<String, Long> mapPlayerNameToPlayerId = challonge.getParticipants(tournament).stream()
                    .collect(Collectors.toMap(item -> item.getName(), item -> item.getId()));

            for (FightResult fightResult : fightResults) {
                Long playerOneId = mapPlayerNameToPlayerId.get(fightResult.getOne().getName());
                Long playerTwoId = mapPlayerNameToPlayerId.get(fightResult.getTwo().getName());
                Match chosenMatch = matches.stream().filter(match -> match.getPlayer1Id().equals(playerOneId) && match.getPlayer2Id().equals(playerTwoId)
                        || match.getPlayer1Id().equals(playerTwoId) && match.getPlayer2Id().equals(playerOneId))
                        .findFirst().orElse(null);

                if(chosenMatch != null) {
                    MatchQuery matchQuery = null;
                    if (playerOneId.equals(chosenMatch.getPlayer1Id())) {
                        matchQuery = getMatchQuery(chosenMatch, fightResult.getResultOne(), fightResult.getResultTwo());
                    } else {
                        matchQuery = getMatchQuery(chosenMatch, fightResult.getResultTwo(), fightResult.getResultOne());
                    }
                    challonge.updateMatch(chosenMatch, matchQuery);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MatchQuery getMatchQuery(Match match, Integer scoreOne, Integer scoreTwo) {
        MatchQuery.MatchQueryBuilder matchQueryBuilder = MatchQuery.builder();
        if (scoreOne == 3 && scoreTwo == 0) {
            matchQueryBuilder.scoresCsv("1-0,1-0,1-0");
            matchQueryBuilder.winnerId(match.getPlayer1Id());
        } else if (scoreOne == 2 && scoreTwo == 0) {
            matchQueryBuilder.scoresCsv("1-0,1-0");
            matchQueryBuilder.winnerId(match.getPlayer1Id());
        } else if (scoreOne == 2 && scoreTwo == 1) {
            matchQueryBuilder.scoresCsv("1-0,0-1,1-0");
            matchQueryBuilder.winnerId(match.getPlayer1Id());
        } else if (scoreOne == 0 && scoreTwo == 3) {
            matchQueryBuilder.scoresCsv("0-1,0-1,0-1");
            matchQueryBuilder.winnerId(match.getPlayer2Id());
        } else if (scoreOne == 0 && scoreTwo == 2) {
            matchQueryBuilder.scoresCsv("0-1,0-1");
            matchQueryBuilder.winnerId(match.getPlayer2Id());
        } else if (scoreOne == 1 && scoreTwo == 2) {
            matchQueryBuilder.scoresCsv("0-1,1-0,0-1");
            matchQueryBuilder.winnerId(match.getPlayer2Id());
        }
        return matchQueryBuilder.build();
    }
}
