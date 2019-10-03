package org.bxtr.PvpBot.commands;

import org.bxtr.PvpBot.model.Player;
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

@Component
@Scope("singleton")
public class FriendCodeListCommand extends BotCommand {

    @Autowired
    private PlayerService playerService;

    public FriendCodeListCommand() {
        super("friends", "Список друзей.");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        List<Player> all = playerService.findAll();
        all.sort((one, two) -> one.getName().compareToIgnoreCase(two.getName()));
        SendMessage sendMessage = new SendMessage().setChatId(chat.getId());
        if(all.size() > 0) {
            final StringBuilder stringBuilder = new StringBuilder();
            all.forEach(player -> {
                stringBuilder.append(player.getName())
                        .append(" `")
                        .append(player.getFriendCode())
                        .append("` \n")
                        .append(player.getInGameNickName())
                        .append(" ")
                        .append(player.getTownName())
                        .append("\n\n");
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
