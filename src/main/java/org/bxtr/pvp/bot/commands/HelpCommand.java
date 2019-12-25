package org.bxtr.pvp.bot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.pvp.bot.Utils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Log4j2
@Component
public class HelpCommand extends BotCommand {

    public HelpCommand() {
        super("help", "Описание некоторых команд.");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), strings));

/*        String stringBuilder = "/new " + AddPlayerCommand.DESCRIPTION + "\n\n" +
                "/add " + AddFightResultCommand.DESCRIPTION + "\n\n" +
                "/all " + AllPlayersCommand.DESCRIPTION + "\n\n" +
                "/results " + AllFightResultCommand.DESCRIPTION + "\n\n" +
                "/leader " + LeaderboardCommand.DESCRIPTION;*/
        SendMessage sendMessage = new SendMessage().setChatId(chat.getId())
                .setText("Подробное описание всех комманд\n" +
                        "https://telegra.ph/Opisanie-komand-bota-10-15");

        Utils.send(absSender, sendMessage);
    }
}
