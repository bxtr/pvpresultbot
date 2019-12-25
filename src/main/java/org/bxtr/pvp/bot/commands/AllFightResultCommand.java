package org.bxtr.pvp.bot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.pvp.bot.repository.FightResultRepositoryJPA;
import org.bxtr.pvp.bot.Utils;
import org.bxtr.pvp.bot.domain.FightResult;
import org.bxtr.pvp.bot.domain.Player;
import org.bxtr.pvp.bot.service.FightResultService;
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
public class AllFightResultCommand extends BotCommand {
    public final static String DESCRIPTION = "Результаты сражений. Без указания игрока - все, с игроком - по фильтру.";

    private final FightResultService fightResultService;

    private final PlayerService playerService;

    private final FightResultRepositoryJPA fightResultRepositoryJPA;

    public AllFightResultCommand(FightResultService fightResultService, PlayerService playerService, FightResultRepositoryJPA fightResultRepositoryJPA) {
        super("results", DESCRIPTION);
        this.fightResultService = fightResultService;
        this.playerService = playerService;
        this.fightResultRepositoryJPA = fightResultRepositoryJPA;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), arguments));
        SendMessage sendMessage = new SendMessage().setChatId(chat.getId());
        if (arguments.length == 0 || arguments.length == 1) {
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
            if (sendMessage.getText().length() > 4000) {
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
        } else {
            sendMessage.setText("Неверное количество аргументов");
            Utils.send(absSender, sendMessage);
        }
    }

}
