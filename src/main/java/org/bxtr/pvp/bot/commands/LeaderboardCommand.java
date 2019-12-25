package org.bxtr.pvp.bot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.pvp.bot.Utils;
import org.bxtr.pvp.bot.domain.FightResult;
import org.bxtr.pvp.bot.domain.Player;
import org.bxtr.pvp.bot.service.FightResultService;
import org.bxtr.pvp.bot.service.PlayerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.*;

@Log4j2
@Component
public class LeaderboardCommand extends BotCommand {
    public final static String DESCRIPTION = "Лидербоард.";

    private final FightResultService fightResultService;

    public LeaderboardCommand(FightResultService fightResultService, PlayerService playerService) {
        super("leader", DESCRIPTION);
        this.fightResultService = fightResultService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), strings));

        List<FightResult> all = fightResultService.findAll();
        Map<Player, Integer> map = new HashMap<>();
        for (FightResult fightResult : all) {
            Player winner = fightResultService.getWinner(fightResult);
            if (!map.containsKey(winner))
                map.put(winner, 0);
            map.put(winner, map.get(winner) + 1);
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
