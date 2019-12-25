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

@Log4j2
@Component
public class AddFightResultShortCommand extends BotCommand {

    private final PlayerService playerService;

    private final AddFightResultCommand addFightResultCommand;

    public AddFightResultShortCommand(PlayerService playerService, AddFightResultCommand addFightResultCommand) {
        super("short", "Добавления результатов бой, без указания первого игрока.");
        this.playerService = playerService;
        this.addFightResultCommand = addFightResultCommand;
    }


    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), strings));
        if (strings != null && strings.length == 3) {
            String userName = user.getUserName();
            Player player = userName != null ? playerService.findPlayer("@" + userName) : null;
            if (player != null) {
                String[] arguments = new String[4];
                arguments[0] = player.getName();
                arguments[1] = strings[0];
                arguments[2] = strings[1];
                arguments[3] = strings[2];
                addFightResultCommand.execute(absSender, user, chat, arguments);
            } else {
                SendMessage sendMessage = new SendMessage()
                        .setChatId(chat.getId())
                        .setText("Вы не зарегистрированы в боте.");

                Utils.send(absSender, sendMessage);
            }
        } else {
            Utils.send(absSender, new SendMessage()
                    .setText("Неверное количество аргументов")
                    .setChatId(chat.getId()));
        }
    }
}
