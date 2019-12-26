package org.bxtr.pvp.bot.commands;

import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.file.Matcher;
import org.bxtr.pvp.bot.domain.Player;
import org.bxtr.pvp.bot.service.PlayerService;
import org.bxtr.pvp.bot.utils.Utils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Log4j2
@Component
public class FriendInfoUpdateCommand extends BotCommand {

    private PlayerService playerService;

    public FriendInfoUpdateCommand(PlayerService playerService) {
        super("updateinfo", "Обновить код дружбы, ник в игре, город");
        this.playerService = playerService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Utils.commandInputToString(user, chat, this.getCommandIdentifier(), arguments);
        SendMessage sendMessage = new SendMessage().setChatId(chat.getId());
        String playerName = "@" + user.getUserName();
        Player player = playerService.findPlayer(playerName);
        if (player == null) {
            sendMessage.setText("Вы еще не зарегестрированы в боте");
            Utils.send(absSender, sendMessage);
            return;
        }

        if (arguments.length == 3) {
            try {
                String friendCode = arguments[0];
                if (!Matcher.match("[0-9]{12}", friendCode, false)) {
                    sendMessage.setText("Код дружбы должен содержать 12 чисел");
                    Utils.send(absSender, sendMessage);
                    return;
                }
                player.setFriendCode(friendCode);

                String playerInGameName = arguments[1];
                if (!Matcher.match("[0-9a-zA-Z]+", playerInGameName, false)) {
                    sendMessage.setText("Игровой ник должен содержать только латинские буквы и цифры");
                    Utils.send(absSender, sendMessage);
                    return;
                }
                player.setInGameNickName(playerInGameName);
                String city = arguments[2];
                player.setTownName(city);

                playerService.updatePlayer(player);

                sendMessage.setText("Информация успешно обновлена");
                Utils.send(absSender, sendMessage);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage.setText("Неверный формат данных. Пример - /updateinfo 111122223333 bxtr2121 Москва");
                Utils.send(absSender, sendMessage);
                return;
            }
        } else {
            sendMessage.setText("Для того, чтобы обновить или добавить свой код дружбы, игровой ник и город, " +
                    "необходимо написать сообщение \n `/updateinfo код_дружбы игровой_ник город`.\n Код дружбы вводится без пробелов. " +
                    "\n Пример: `/updateinfo 111122223333 bxtr2121 Москва`").enableMarkdown(true);
            Utils.send(absSender, sendMessage);
        }
    }
}
