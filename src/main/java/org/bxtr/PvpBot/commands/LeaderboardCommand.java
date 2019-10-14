package org.bxtr.PvpBot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.Utils;
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

@Log4j2
@Component
@Scope("singleton")
public class LeaderboardCommand extends BotCommand {
    public final static String DESCRIPTION = "Лидербоард.";

    private final FightResultService fightResultService;
    private final PlayerService playerService;

    public LeaderboardCommand(FightResultService fightResultService, PlayerService playerService) {
        super("leader", DESCRIPTION);
        this.fightResultService = fightResultService;
        this.playerService = playerService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), strings));

        List<FightResult> all = fightResultService.findAll();
        Map<Player, Integer> map = new HashMap<>();
        for (FightResult fightResult : all) {
            if (!map.containsKey(fightResult.getWinner()))
                map.put(fightResult.getWinner(), 0);
            map.put(fightResult.getWinner(), map.get(fightResult.getWinner()) + 1);
        }

        Map<Integer, List<Player>> resultMap = new TreeMap<>(Comparator.reverseOrder());
        for (Map.Entry<Player, Integer> entry : map.entrySet()) {
            if (!resultMap.containsKey(entry.getValue()))
                resultMap.put(entry.getValue(), new ArrayList<>());
            resultMap.get(entry.getValue()).add(entry.getKey());
        }

        final StringBuilder stringBuilder = new StringBuilder();
        int place = 1;
        for (Map.Entry<Integer, List<Player>> entry : resultMap.entrySet()) {
            stringBuilder.append("#").append(place).append(" ");
            place++;
            entry.getValue().forEach(player -> stringBuilder.append(player.getName()).append(" "));
            stringBuilder.append("- ").append(Integer.toString(entry.getKey())).append("\n");
        }

        SendMessage sendMessage = new SendMessage().setChatId(chat.getId())
                .setText(stringBuilder.toString().length() > 0 ? stringBuilder.toString() : "Пока пусто");
        Utils.send(absSender, sendMessage);
    }
}
