package org.bxtr.PvpBot;

import org.bxtr.PvpBot.commands.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

@Component
@Scope("singleton")
public class PvpBot extends TelegramLongPollingCommandBot {

    @Autowired
    private TelegramBotsApi telegramBotsApi;
    @Autowired
    private AddPlayerCommand addPlayerCommand;
    @Autowired
    private AddFightResultCommand addFightResultCommand;
    @Autowired
    private AllFightResultCommand allFightResultCommand;
    @Autowired
    private AllPlayersCommand allPlayersCommand;
    @Autowired
    private HelpCommand helpCommand;
    @Autowired
    private LeaderboardCommand leaderboardCommand;
    @Autowired
    private UpdateResultsOnChallongeCommand updateResultsOnChallongeCommand;
    @Autowired
    private FriendCodeListCommand friendCodeListCommand;

    @Value("${pvpbot.telegram.token}")
    private String TOKEN;

    public PvpBot(@Autowired DefaultBotOptions options) {
        super(options, "PvpResultBot");

        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId());
            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot.");
            try {
                execute(commandUnknownMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }

    @PostConstruct
    private void postConstruct() {
        try {
            telegramBotsApi.registerBot(this);
            register(addPlayerCommand);
            register(addFightResultCommand);
            register(allPlayersCommand);
            register(allFightResultCommand);
            register(helpCommand);
            register(leaderboardCommand);
            register(updateResultsOnChallongeCommand);
            register(friendCodeListCommand);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            System.out.println(message);
            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId(), message + "1");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
