package org.bxtr.PvpBot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.Utils;
import org.bxtr.PvpBot.model.Player;
import org.bxtr.PvpBot.service.PlayerService;
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
public class RegisterPlayerCommand extends BotCommand {

    private PlayerService playerService;

    public RegisterPlayerCommand(PlayerService playerService) {
        super("register", "Добавит участника в бота");
        this.playerService = playerService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String userName = "@" + Utils.safeToString(user.getUserName());
        Player player = playerService.findPlayer(userName);
        SendMessage sendMessage = new SendMessage()
                .setChatId(chat.getId());
        if (player == null) {
            playerService.createPlayer(userName);
            sendMessage.setText(String.format("%s добавлен", userName));
        } else {
            sendMessage.setText(String.format("%s уже зарегистрирован", userName));
        }
        Utils.send(absSender, sendMessage);
    }
}
