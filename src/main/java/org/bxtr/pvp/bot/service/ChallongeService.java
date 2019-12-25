package org.bxtr.pvp.bot.service;

import at.stefangeyer.challonge.Challonge;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.Tournament;
import at.stefangeyer.challonge.model.query.MatchQuery;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bxtr.pvp.bot.repository.TournamentParticipantRepository;
import org.bxtr.pvp.bot.domain.FightResult;
import org.bxtr.pvp.bot.domain.Player;
import org.bxtr.pvp.bot.domain.TournamentParticipant;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class ChallongeService {

    private final FightResultService fightResultService;
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final PlayerService playerService;
    private final TournamentService tournamentService;
    private final Challonge challonge;

    public void update(boolean forceUpdate) {
        List<FightResult> unregistredFightResults;
        if (forceUpdate) {
            unregistredFightResults = fightResultService.findAll();
        } else {
            unregistredFightResults = fightResultService.findAll().stream()
                    .filter(item -> item.getRegistered() == null || !item.getRegistered())
                    .collect(Collectors.toList());
        }
        if (unregistredFightResults.size() == 0)
            return;

        try {
            Tournament tournament = challonge.getTournaments().stream()
                    .filter(item -> item.getName().equals(tournamentService.getCurrentTournament()))
                    .findFirst().orElse(null);

            List<Match> matches = challonge.getMatches(tournament);
            Map<String, Long> mapPlayerNameToPlayerId = challonge.getParticipants(tournament).stream()
                    .collect(Collectors.toMap(Participant::getName, Participant::getId));

            for (FightResult fightResult : unregistredFightResults) {
                Long playerOneId = mapPlayerNameToPlayerId.get(fightResult.getOne().getName());
                Long playerTwoId = mapPlayerNameToPlayerId.get(fightResult.getTwo().getName());
                Match chosenMatch = matches.stream().filter(match -> match.getPlayer1Id().equals(playerOneId) && match.getPlayer2Id().equals(playerTwoId)
                        || match.getPlayer1Id().equals(playerTwoId) && match.getPlayer2Id().equals(playerOneId))
                        .findFirst().orElse(null);

                if (chosenMatch != null) {
                    MatchQuery matchQuery = null;
                    if (playerOneId.equals(chosenMatch.getPlayer1Id())) {
                        matchQuery = getMatchQuery(chosenMatch, fightResult.getResultOne(), fightResult.getResultTwo());
                    } else {
                        matchQuery = getMatchQuery(chosenMatch, fightResult.getResultTwo(), fightResult.getResultOne());
                    }
                    challonge.updateMatch(chosenMatch, matchQuery);
                    fightResult.setRegistered(true);
                    fightResultService.saveResult(fightResult);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<String> getParticipant() {
        try {
            Tournament tournament = challonge.getTournaments().stream()
                    .filter(item -> item.getName().equals(tournamentService.getCurrentTournament()))
                    .findFirst().orElse(null);

            Set<String> participantInChallonge = challonge.getParticipants(tournament).stream()
                    .map(item -> item.getName().toLowerCase())
                    .collect(Collectors.toSet());

            org.bxtr.pvp.bot.domain.Tournament currentTournament = tournamentService.getCurrentTournament();
            Set<String> participantInDB = tournamentParticipantRepository
                    .findByTournament(currentTournament)
                    .stream().map(participant -> participant.getPlayer().getName())
                    .collect(Collectors.toSet());

            Set<String> notAdded = participantInChallonge.stream()
                    .filter(name -> !participantInDB.contains(name))
                    .collect(Collectors.toSet());


            Set<String> addedPlayers = new HashSet<>();

            notAdded.forEach(playerNickname -> {
                Player player = playerService.findPlayer(playerNickname);
                if (player == null)
                    return;
                TournamentParticipant tournamentParticipant = new TournamentParticipant();
                tournamentParticipant.setPlayer(player);
                tournamentParticipant.setTournament(currentTournament);
                tournamentParticipantRepository.save(tournamentParticipant);
                log.info(String.format("Player %s is added", playerNickname));
                addedPlayers.add(playerNickname);
            });

            return addedPlayers;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptySet();
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
