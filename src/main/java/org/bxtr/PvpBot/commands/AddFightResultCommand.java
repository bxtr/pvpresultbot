package org.bxtr.PvpBot.commands;

import org.bxtr.PvpBot.model.FightResult;
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

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("singleton")
public class AddFightResultCommand extends BotCommand {
    public final static String DESCRIPTION = "добавить результат сражения. Для этого надо, чтобы оба игрока были зарегистрированны. " +
            "\n Пример: /add @bxtr21 @pvpoke 1 2";

    @Autowired
    private PlayerService playerService;

    @Autowired
    private FightResultService fightResultService;

    public AddFightResultCommand() {
        super("add", DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage sendMessage = new SendMessage().setChatId(chat.getId());
        List<String> errrors = validateInput(arguments);
        if (errrors.size() == 0) {
            FightResult fightResult = new FightResult()
                    .setOne(playerService.findPlayer(arguments[0]))
                    .setTwo(playerService.findPlayer(arguments[1]))
                    .setResultOne(Integer.parseInt(arguments[2]))
                    .setResultTwo(Integer.parseInt(arguments[3]))
                    .setRegistered(false);

            fightResultService.saveResult(fightResult);
            sendMessage.setText("Результат добавлен");
        } else {
            final StringBuilder stringBuilder = new StringBuilder();
            errrors.forEach(item -> stringBuilder.append(item).append(";\n"));
            sendMessage.setText(stringBuilder.toString());
        }

        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public List<String> validateInput(String[] arguments) {
        List<String> errors = new ArrayList<>();
        if (arguments != null && arguments.length == 4) {
            try {
                String player1 = arguments[0];
                String player2 = arguments[1];
                int scorePlayer1 = Integer.parseInt(arguments[2]);
                int scorePlayer2 = Integer.parseInt(arguments[3]);
                if (!checkPlayer(player1.toLowerCase()))
                    errors.add("первый игрок не найден. возможно не правильно написан ник или он не заргестрирован.");
                if (!checkPlayer(player2.toLowerCase()))
                    errors.add("второй игрок не найден. возможно не правильно написан ник или он не заргестрирован.");
                if (!checkScore(scorePlayer1))
                    errors.add("счет для первого игрока неправильно указан, допустимо указывать значение от 0 до 3");
                if (!checkScore(scorePlayer2))
                    errors.add("счет для второго игрока неправильно указан, допустимо указывать значение от 0 до 3");
                if (!((scorePlayer1 + scorePlayer2) < 4))
                    errors.add("сумма результатов игроков не должна быть больше 3");
                if (!((scorePlayer1 + scorePlayer2) >= 2))
                    errors.add("сумма результатов игроков должна быть больше 1");
                if (scorePlayer1 == scorePlayer2)
                    errors.add("у игроков должен быть различное количество выигранных раундов");

            } catch (Exception e) {
                errors.add("неверный формат переменных, возможно, ник и счет стоят не на своих местах");
            }
        } else {
            errors.add("неверное количество аргументов");
        }
        return errors;
    }

    private boolean checkPlayer(String player) {
        return playerService.findPlayer(player) != null;
    }

    private boolean checkScore(int score) {
        return score >= 0 && score < 4;
    }
}
