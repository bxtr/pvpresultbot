package org.bxtr.pvp.bot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.pvp.bot.domain.FightResult;
import org.bxtr.pvp.bot.repository.FightResultRepositoryJPA;
import org.bxtr.pvp.bot.service.FightResultService;
import org.bxtr.pvp.bot.service.PlayerService;
import org.bxtr.pvp.bot.utils.Utils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class AddFightResultCommand extends BotCommand {
    public final static String DESCRIPTION = "Добавить результат сражения. Для этого надо, чтобы оба игрока были зарегистрированны. Пример: /add @bxtr21 @pvpoke 1 2";

    private final PlayerService playerService;

    private final FightResultRepositoryJPA fightResultRepositoryJPA;

    private final FightResultService fightResultService;

    public AddFightResultCommand(PlayerService playerService, FightResultRepositoryJPA fightResultRepositoryJPA, FightResultService fightResultService) {
        super("add", DESCRIPTION);
        this.playerService = playerService;
        this.fightResultRepositoryJPA = fightResultRepositoryJPA;
        this.fightResultService = fightResultService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), arguments));
        SendMessage sendMessage = new SendMessage().setChatId(chat.getId());
        List<String> errrors = validateInput(arguments);
        if (errrors.size() == 0) {
            fightResultService.saveResult(playerService.findPlayer(arguments[0]),
                    playerService.findPlayer(arguments[1]),
                    Integer.parseInt(arguments[2]),
                    Integer.parseInt(arguments[3])
            );
            sendMessage.setText("Результат добавлен");
        } else {
            final StringBuilder stringBuilder = new StringBuilder();
            errrors.forEach(item -> stringBuilder.append(item).append(".\n\n"));
            sendMessage.setText(stringBuilder.toString());
        }

        Utils.send(absSender, sendMessage);
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
                    errors.add("Первый игрок не найден. Возможно, не правильно написан ник или он не зарегестрирован");
                if (!checkPlayer(player2.toLowerCase()))
                    errors.add("Второй игрок не найден. Возможно, не правильно написан ник или он не зарегестрирован");
                if (player1.equals(player2))
                    errors.add("Нельзя добавить результаты сражений между одним и тем же человеком");
                if (!checkScore(scorePlayer1))
                    errors.add("Счет для первого игрока неправильно указан, допустимо указывать значение от 0 до 3");
                if (!checkScore(scorePlayer2))
                    errors.add("Счет для второго игрока неправильно указан, допустимо указывать значение от 0 до 3");
                if (!((scorePlayer1 + scorePlayer2) < 4))
                    errors.add("Сумма результатов игроков не должна быть больше 3");
                if (!((scorePlayer1 + scorePlayer2) >= 2))
                    errors.add("Сумма результатов игроков должна быть больше 1");
                if (scorePlayer1 == scorePlayer2)
                    errors.add("у игроков должно быть различное количество выигранных раундов");

                List<FightResult> fightResultWith = fightResultRepositoryJPA.findFightResultWith(player1, player2);
                if (fightResultWith.size() > 0) {
                    errors.add("Для этих игроков результат уже был добавлен ранее");
                }
            } catch (Exception e) {
                errors.add("Неверный формат переменных, возможно, ник и счет стоят не на своих местах");
            }
        } else {
            errors.add("Неверное количество аргументов");
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
