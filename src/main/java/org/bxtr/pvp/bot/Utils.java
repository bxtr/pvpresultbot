package org.bxtr.pvp.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Utils {

    public static String safeToString(String str) {
        return str != null ? str : null;
    }

    public static String commandInputToString(User user, Chat chat, String command, String[] arguments) {
        StringBuilder stringBuilder = new StringBuilder().append("User:");
        if (user != null)
            stringBuilder.append(Utils.safeToString(user.getUserName()));
        else
            stringBuilder.append("###");
        stringBuilder.append(" Command:").append(Utils.safeToString(command));
        stringBuilder.append(" Arg:");
        if (arguments.length > 0)
            for (String str : arguments)
                stringBuilder.append(Utils.safeToString(str)).append(";");
        else
            stringBuilder.append("###");
        return stringBuilder.toString();
    }

    public static void send(AbsSender absSender, SendMessage sendMessage) {
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /*    private InlineKeyboardMarkup getInlineKeyboardMarkup(String playerName) {
        InlineKeyboardButton button = new InlineKeyboardButton()
                .setText("Регистрация").setCallbackData(playerName);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);
        return new InlineKeyboardMarkup()
                .setKeyboard(keyboard);
    }*/
}
