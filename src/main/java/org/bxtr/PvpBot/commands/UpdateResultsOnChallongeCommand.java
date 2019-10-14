package org.bxtr.PvpBot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.Utils;
import org.bxtr.PvpBot.service.ChallongeService;
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
public class UpdateResultsOnChallongeCommand extends BotCommand {

    private final ChallongeService challongeService;

    public UpdateResultsOnChallongeCommand(ChallongeService challongeService) {
        super("update", "Обновит результаты на Challonge.");
        this.challongeService = challongeService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), arguments));
        challongeService.update();
        try {
            absSender.execute(new SendMessage().setChatId(chat.getId()).setText("done."));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
