package org.bxtr.PvpBot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.Utils;
import org.bxtr.PvpBot.model.FightResult;
import org.bxtr.PvpBot.model.Player;
import org.bxtr.PvpBot.repository.FightResultRepositoryJPA;
import org.bxtr.PvpBot.service.FightResultService;
import org.bxtr.PvpBot.service.PlayerService;
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

@Log4j2
@Component
@Scope("singleton")
public class AllFightResultCommand extends BotCommand {
    public final static String DESCRIPTION = "Результаты сражений. Без указания игрока - все, с игроком - по фильтру.";

    @Autowired
    private FightResultService fightResultService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private FightResultRepositoryJPA fightResultRepositoryJPA;

    public AllFightResultCommand() {
        super("results", DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), arguments));
        SendMessage sendMessage = new SendMessage().setChatId(chat.getId());
        if (arguments.length == 0) {
            List<FightResult> results = fightResultService.findAll();
            if (results.size() > 0) {
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
        } else if (arguments.length == 1) {
            Player player = playerService.findPlayer(arguments[0]);
            if (player != null) {
                List<FightResult> fightResultWith = fightResultRepositoryJPA.findFightResultWith(player.getName());
                final StringBuilder stringBuilder = new StringBuilder();
                fightResultWith.forEach(result -> {
                    if (result.getOne().equals(player)) {
                        stringBuilder.append(result.getOne().getName())
                                .append(" ").append(result.getResultOne())
                                .append(":")
                                .append(result.getResultTwo()).append(" ")
                                .append(result.getTwo().getName())
                                .append("\n");
                    } else {
                        stringBuilder.append(result.getTwo().getName())
                                .append(" ").append(result.getResultTwo())
                                .append(":")
                                .append(result.getResultOne()).append(" ")
                                .append(result.getOne().getName())
                                .append("\n");
                    }
                });
                sendMessage.setText(stringBuilder.toString());
            } else {
                sendMessage.setText("Игрок не найден.");
            }
        }

        //TODO сделать получше.
        if(sendMessage.getText().length() > 4000) {
            String text = sendMessage.getText();
            int index = text.indexOf("\n", 3900);
            String firstSubstring = text.substring(0, index);
            String secondSubstring = text.substring(index);
            SendMessage sendMessageOne = new SendMessage().setText(firstSubstring).setChatId(chat.getId());
            SendMessage sendMessageTwo = new SendMessage().setText(secondSubstring).setChatId(chat.getId());
            Utils.send(absSender, sendMessageOne);
            Utils.send(absSender, sendMessageTwo);
        } else {
            Utils.send(absSender, sendMessage);
        }

    }

}
