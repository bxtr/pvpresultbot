package org.bxtr.pvp.bot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.pvp.bot.Utils;
import org.bxtr.pvp.bot.domain.Player;
import org.bxtr.pvp.bot.service.PlayerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Log4j2
@Component
public class AllPlayersCommand extends BotCommand {
    public final static String DESCRIPTION = "Все зарегистрированные игроки.";

    private final PlayerService playerService;

    public AllPlayersCommand(PlayerService playerService) {
        super("all", DESCRIPTION);
        this.playerService = playerService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), arguments));
        List<Player> players = playerService.findAll();
        SendMessage sendMessage = new SendMessage()
                .setChatId(chat.getId());
        if (players.size() > 0) {
            final StringBuilder stringBuilder = new StringBuilder();
            players.forEach(player -> {
                stringBuilder.append(player.getName()).append("\n");
            });
            sendMessage.setText(stringBuilder.toString());
        } else {
            sendMessage.setText("Пока пусто");
        }

        Utils.send(absSender, sendMessage);
    }
}
