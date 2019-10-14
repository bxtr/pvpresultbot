package org.bxtr.PvpBot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.Utils;
import org.bxtr.PvpBot.model.Player;
import org.bxtr.PvpBot.service.PlayerService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Log4j2
@Component
@Scope("singleton")
public class AddPlayerCommand extends BotCommand {
    public final static String DESCRIPTION = "Добавить нового игрока. Добавлять через ник в телеге. Пример: /new @bxtr21";

    private final PlayerService playerService;

    public AddPlayerCommand(PlayerService playerService) {
        super("new", DESCRIPTION);
        this.playerService = playerService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), arguments));
        SendMessage sendMessage = new SendMessage()
                .setChatId(chat.getId());
        if (arguments != null && arguments.length == 1) {
            if (arguments[0] == null)
                return;
            String name = arguments[0].toLowerCase();
            Player player = playerService.findPlayer(name);
            if (player == null) {
                if (arguments[0].startsWith("@")) {
                    playerService.createPlayer(arguments[0]);
                    sendMessage.setText(arguments[0] + " is added!");
                } else {
                    sendMessage.setText("Ник в телеграмме начинается с @");
                }
            } else {
                sendMessage.setText("Этот игрок уже добавлен");
            }
        } else {
            sendMessage.setText("Incorrect input");
        }

        Utils.send(absSender, sendMessage);
    }
}
