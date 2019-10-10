package org.bxtr.PvpBot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.Utils;
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

@Log4j2
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
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), strings));
        List<Player> all = playerService.findAll();
        all.sort((one, two) -> one.getName().compareToIgnoreCase(two.getName()));
        SendMessage sendMessage = new SendMessage().setChatId(chat.getId());
        if(all.size() > 0) {
            final StringBuilder stringBuilder = new StringBuilder();
            all.forEach(player -> {
                stringBuilder.append(Utils.safeToString(player.getName()))
                        .append(" `")
                        .append(Utils.safeToString(player.getFriendCode()))
                        .append("` \n")
                        .append(Utils.safeToString(player.getInGameNickName()))
                        .append(" ")
                        .append(Utils.safeToString(player.getTownName()))
                        .append("\n\n");
            });
            sendMessage.setText(characterEscaping(stringBuilder.toString()));
            sendMessage.enableMarkdown(true);
        } else {
            sendMessage.setText("Пока пусто");
        }

        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private String characterEscaping(String str) {
        return str.replaceAll("_", "\\\\_");
    }
}
