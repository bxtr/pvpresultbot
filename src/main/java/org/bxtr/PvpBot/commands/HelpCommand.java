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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/new ").append(AddPlayerCommand.DESCRIPTION).append("\n\n")
                .append("/add ").append(AddFightResultCommand.DESCRIPTION).append("\n\n")
                .append("/all ").append(AllPlayersCommand.DESCRIPTION).append("\n\n")
                .append("/results ").append(AllFightResultCommand.DESCRIPTION).append("\n\n")
                .append("/leader ").append(LeaderboardCommand.DESCRIPTION);

        SendMessage sendMessage = new SendMessage().setChatId(chat.getId())
                .setText(stringBuilder.toString());

        Utils.send(absSender, sendMessage);
    }
}
