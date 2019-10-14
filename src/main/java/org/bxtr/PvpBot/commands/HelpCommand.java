package org.bxtr.PvpBot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.Utils;
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
public class HelpCommand extends BotCommand {

    public HelpCommand() {
        super("help", "Описание некоторых команд.");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), strings));

        String stringBuilder = "/new " + AddPlayerCommand.DESCRIPTION + "\n\n" +
                "/add " + AddFightResultCommand.DESCRIPTION + "\n\n" +
                "/all " + AllPlayersCommand.DESCRIPTION + "\n\n" +
                "/results " + AllFightResultCommand.DESCRIPTION + "\n\n" +
                "/leader " + LeaderboardCommand.DESCRIPTION;
        SendMessage sendMessage = new SendMessage().setChatId(chat.getId())
                .setText(stringBuilder);

        Utils.send(absSender, sendMessage);
    }
}
