package org.bxtr.PvpBot.commands;

import org.bxtr.PvpBot.model.FightResult;
import org.bxtr.PvpBot.model.Player;
import org.bxtr.PvpBot.service.FightResultService;
import org.bxtr.PvpBot.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class LeaderboardCommand extends BotCommand {
    public final static String DESCRIPTION = "лидербоард";

    @Autowired
    private FightResultService fightResultService;

    @Autowired
    private PlayerService playerService;

    public LeaderboardCommand() {
        super("leader", DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        List<FightResult> all = fightResultService.findAll();

        final Map<Long, Integer> mapPlayerIdToScore = new LinkedHashMap<>();
        all.stream()
                .map(fight -> fight.getWinner())
                .forEach(player -> {
                    if (!mapPlayerIdToScore.containsKey(player.getId()))
                        mapPlayerIdToScore.put(player.getId(), 0);
                    mapPlayerIdToScore.put(player.getId(), mapPlayerIdToScore.get(player.getId()) + 1);
                });

        final Map<Long, Player> playerMap = playerService.findAll().stream()
                .collect(Collectors.toMap(player -> player.getId(), player -> player));

        List<HolderWinner> holderWinners = new ArrayList<>();
        for(Long playerId : mapPlayerIdToScore.keySet()) {
            HolderWinner holderWinner = new HolderWinner()
                    .setWinner(playerMap.get(playerId))
                    .setScore(mapPlayerIdToScore.get(playerId));
            holderWinners.add(holderWinner);
        }

        Collections.sort(holderWinners, (item1, item2) -> item1.getScore().compareTo(item2.getScore()));

        final StringBuilder stringBuilder = new StringBuilder();

        holderWinners
                .forEach(entry -> {
                    Player player = entry.getWinner();
                    stringBuilder.append(player.getName())
                            .append(" - ").append(entry.getScore())
                            .append("\n");
                });

        SendMessage sendMessage = new SendMessage().setChatId(chat.getId())
                .setText(stringBuilder.toString());
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    class HolderWinner {

        private HolderWinner(){
            //empty
        }

        Player winner;
        Integer score;

        public Player getWinner() {
            return winner;
        }

        public HolderWinner setWinner(Player winner) {
            this.winner = winner;
            return this;
        }

        public Integer getScore() {
            return score;
        }

        public HolderWinner setScore(Integer score) {
            this.score = score;
            return this;
        }
    }
}
