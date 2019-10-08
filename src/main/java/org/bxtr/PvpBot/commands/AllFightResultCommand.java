package org.bxtr.PvpBot.commands;

import org.bxtr.PvpBot.model.FightResult;
import org.bxtr.PvpBot.service.FightResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@Scope("singleton")
public class AllFightResultCommand extends BotCommand {
    public final static String DESCRIPTION = "результаты всех сражений";

    @Autowired
    private FightResultService fightResultService;

    public AllFightResultCommand() {
        super("results", DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        List<FightResult> results = fightResultService.findAll();
        SendMessage sendMessage = new SendMessage()
                .setChatId(chat.getId());
        if(results.size() > 0) {
            final StringBuilder stringBuilder = new StringBuilder();
            results.forEach(result -> {
                stringBuilder.append(result.getOne().getName())
                        .append(" ").append(result.getResultOne())
                        .append(":")
                        .append(result.getResultTwo()).append(" ")
                        .append(result.getTwo().getName())
                        .append("\n");
            });
            sendMessage.setText(stringBuilder.toString());
        } else {
            sendMessage.setText("Пока пусто");
        }

        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
