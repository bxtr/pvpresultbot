package org.bxtr.PvpBot.commands;

import org.bxtr.PvpBot.service.ChallongeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Scope("singleton")
public class UpdateResultsOnChallongeCommand extends BotCommand {

    @Autowired
    private ChallongeService challongeService;

    public UpdateResultsOnChallongeCommand() {
        super("update", "обновит результаты на Challonge");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        challongeService.update();
        try {
            absSender.execute(new SendMessage().setChatId(chat.getId()).setText("done."));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
